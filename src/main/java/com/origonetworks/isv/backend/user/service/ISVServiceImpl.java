package com.origonetworks.isv.backend.user.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.origonetworks.isv.backend.core.dao.GenericDAO;
import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectEventService;
import com.origonetworks.isv.backend.integration.remote.type.ErrorCode;
import com.origonetworks.isv.backend.integration.remote.type.EventType;
import com.origonetworks.isv.backend.integration.remote.vo.APIResult;
import com.origonetworks.isv.backend.integration.remote.vo.AccountSummary;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.integration.remote.vo.RemoteEvent;
import com.origonetworks.isv.backend.user.model.Account;
import com.origonetworks.isv.backend.user.model.User;
import com.origonetworks.isv.backend.user.util.BeanUtils;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;

@Service("isvService")
public class ISVServiceImpl implements ISVService {
	private static final Logger log = Logger.getLogger(ISVServiceImpl.class);

	private static final String ZIP_CODE_KEY = "zipCode";
	private static final String DEPARTMENT_KEY = "department";
	private static final String TIMEZONE_KEY = "timezone";
	private static final String APP_ADMIN = "appAdmin";

	private GenericDAO<Account, Long> accountDao;
	private GenericDAO<User, Long> userDao;

	@Autowired
	public void setAccountDao(GenericDAO<Account, Long> accountDao) {
		this.accountDao = accountDao;
	}

	@Autowired
	public void setUserDao(GenericDAO<User, Long> userDao) {
		this.userDao = userDao;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Long create(AccountBean accountBean, UserBean adminBean) {
		User admin = BeanUtils.createPojo(adminBean);
		admin.setAdmin(true);
		Account account = BeanUtils.createPojo(accountBean);
		admin.setAccount(account);
		account.getUsers().add(admin);
		this.accountDao.saveOrUpdate(account);
		return account.getId();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void create(UserBean userBean, AccountBean accountBean) {
		Account account = readAccount(accountBean);
		User user = BeanUtils.createPojo(userBean);
		user.setAccount(account);
		account.getUsers().add(user);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<UserBean> readUsers() {
		List<UserBean> userBeans = new ArrayList<UserBean>();
		List<User> users = this.userDao.findAll();
		for (User user : users) {
			userBeans.add(BeanUtils.createBean(user));
		}
		return userBeans;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public UserBean readUser(Long userId) {
		return BeanUtils.createBean(this.userDao.findById(userId));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public AccountBean readAccount(Long accountId) {
		return BeanUtils.createBean(accountDao.findById(accountId));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public AccountBean readAccount(UserBean userBean) {
		try {
			User user = userDao.findById(userBean.getId());
			return BeanUtils.createBean(user.getAccount());
		} catch (ObjectNotFoundException onfe) {
			return null;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<AccountBean> readAccounts() {
		List<AccountBean> accountBeans = new ArrayList<AccountBean>();
		for (Account account : accountDao.findAll()) {
			accountBeans.add(BeanUtils.createBean(account));
		}
		return accountBeans;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public AccountSummary readAccountSummary(String accountIdentifier) {
		Account account = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class).add(Property.forName("identifier").eq(accountIdentifier));
		List<Account> accounts = this.accountDao.findByCriteria(criteria);
		if (accounts != null && accounts.size() == 1) {
			account = accounts.get(0);
		}
		if (account == null) {
			return null;
		}
		AccountSummary accountSummary = new AccountSummary();
		accountSummary.setEditionCode(account.getEditionCode());
		accountSummary.setMaxNumOfUsers(account.getMaxUsers());
		accountSummary.setNumOfUsers(Integer.valueOf(account.getUsers().size()));
		for (User user : account.getUsers()) {
			if (user.getOpenId() != null) {
				if (accountSummary.getSyncedOpenIds() == null) {
					accountSummary.setSyncedOpenIds(new ArrayList<String>());
				}
				accountSummary.getSyncedOpenIds().add(user.getOpenId());
			}
		}
		return accountSummary;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(UserBean userBean) {
		User user = readUser(userBean);
		BeanUtils.populateAttributes(userBean, user);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(AccountBean accountBean) {
		Account account = readAccount(accountBean);
		BeanUtils.populateAttributes(accountBean, account);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(AccountBean accountBean) {
		Account account = readAccount(accountBean);
		this.accountDao.delete(account);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(UserBean userBean) {
		User user = readUser(userBean);
		user.getAccount().getUsers().remove(user);
		user.setAccount(null);
		this.userDao.delete(user);
	}

	private Account readAccount(AccountBean accountBean) {
		Account account = null;
		if (accountBean.getId() != null) {
			account = this.accountDao.findById(accountBean.getId());
		} else if (accountBean.getIdentifier() != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Account.class).add(Property.forName("identifier").eq(accountBean.getIdentifier()));
			List<Account> accounts = this.accountDao.findByCriteria(criteria);
			if (accounts != null && accounts.size() == 1) {
				account = accounts.get(0);
			}
			if (account == null) {
				throw new ObjectNotFoundException(accountBean.getIdentifier(), Account.class.toString());
			}
		}
		return account;
	}

	private User readUser(UserBean userBean) {
		User user = null;
		if (userBean.getId() != null) {
			user = this.userDao.findById(userBean.getId());
		} else if (userBean.getOpenId() != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(User.class).add(Property.forName("openId").eq(userBean.getOpenId()));
			List<User> users = this.userDao.findByCriteria(criteria);
			if (users != null && users.size() == 1) {
				user = users.get(0);
			}
			if (user == null) {
				throw new ObjectNotFoundException(userBean.getOpenId(), User.class.toString());
			}
		}
		return user;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public APIResult processEvent(String token) {
		APIResult result = new APIResult();
		result.setSuccess(true);

		AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
		final EventInfo eventInfo = service.readInfo(token);
		if (eventInfo == null) {
			result.setSuccess(false);
			result.setErrorCode(ErrorCode.UNKNOWN_ERROR);
			result.setMessage("No event info found.");
			return result;
		}
		if (eventInfo.getType() == EventType.USER_ASSIGNMENT || eventInfo.getType() == EventType.USER_UNASSIGNMENT) {
			UserBean userBean = new UserBean();
			userBean.setOpenId(eventInfo.getPayload().getUser().getOpenId());
			AccountBean accountBean = new AccountBean();
			accountBean.setIdentifier(eventInfo.getPayload().getAccount().getAccountIdentifier());
			if (eventInfo.getType() == EventType.USER_ASSIGNMENT) {
				// Read extra info about the user.
				userBean.setEmail(eventInfo.getPayload().getUser().getEmail());
				userBean.setFirstName(eventInfo.getPayload().getUser().getFirstName());
				userBean.setLastName(eventInfo.getPayload().getUser().getLastName());
				if (eventInfo.getPayload().getUser().getAttributes() != null) {
					userBean.setZipCode(eventInfo.getPayload().getUser().getAttributes().get(ZIP_CODE_KEY));
					userBean.setDepartment(eventInfo.getPayload().getUser().getAttributes().get(DEPARTMENT_KEY));
					userBean.setTimezone(eventInfo.getPayload().getUser().getAttributes().get(TIMEZONE_KEY));
				}
				boolean appAdminValue =
					Boolean.parseBoolean(eventInfo.getPayload().getUser().getAttributes().get(APP_ADMIN));
				userBean.setAdmin(appAdminValue);
				if (userBean.getFirstName() != null && userBean.getLastName() != null) {
					userBean.setUsername(userBean.getFirstName().substring(0, 1).toLowerCase() + userBean.getLastName().toLowerCase() + "_" + (new Date()).getTime());
					userBean.setPassword("password");
				}
				// AppDirect is trying to create a new user.
				if (isUserExists(userBean.getOpenId(), userBean.getEmail())) {
					// A user with the same OpenID or email address already exists. Fail.
					result.setSuccess(false);
					result.setErrorCode(ErrorCode.USER_ALREADY_EXISTS);
					result.setMessage("A user with this OpenID or email already exists.");
				} else {
					try {
						// Create the new user.
						create(userBean, accountBean);
						result.setMessage("Successfully created user: " + userBean.getUsername());
					} catch (ObjectNotFoundException onfe) {
						// The account could not be found. Fail.
						result.setSuccess(false);
						result.setErrorCode(ErrorCode.ACCOUNT_NOT_FOUND);
						result.setMessage(onfe.getMessage());
					}
				}
			} else if (eventInfo.getType() == EventType.USER_UNASSIGNMENT) {
				// AppDirect is trying to delete a user.
				try {
					User user = readUser(userBean);
					if (!StringUtils.equals(accountBean.getIdentifier(), user.getAccount().getIdentifier())) {
						// The user account is not the same as the account passed in. We can't allow that. Fail.
						result.setSuccess(false);
						result.setErrorCode(ErrorCode.UNAUTHORIZED);
						result.setMessage("User does not belong to the expected account.");
					} else {
						user.getAccount().getUsers().remove(user);
						user.setAccount(null);
						this.userDao.delete(user);
						result.setMessage("Successfully deleted user: " + userBean.getOpenId());
					}
				} catch (ObjectNotFoundException onfe) {
					// The user could not be found. Fail.
					result.setSuccess(false);
					result.setErrorCode(ErrorCode.USER_NOT_FOUND);
					result.setMessage(onfe.getMessage());
				}
			}
		} else if (eventInfo.getType() == EventType.ACCOUNT_UNSYNC) {
			// A previously synced account is being unsynced.
			int numOfUsers = 0;
			String accountIdentifier = eventInfo.getPayload().getAccount().getAccountIdentifier();
			AccountBean accountBean = new AccountBean();
			accountBean.setIdentifier(accountIdentifier);
			try {
				Account account = readAccount(accountBean);
				// Clear the user OpenIDs.
				for (User user : account.getUsers()) {
					user.setOpenId(null);
					numOfUsers++;
				}
				result.setMessage("Successfully un-synced " + String.valueOf(numOfUsers) + " user(s).");
			} catch (ObjectNotFoundException onfe) {
				// The account could not be found. Fail.
				result.setSuccess(false);
				result.setErrorCode(ErrorCode.ACCOUNT_NOT_FOUND);
				result.setMessage("Account does not exist: " + accountIdentifier);
			}
		} else {
			result.setSuccess(false);
			result.setErrorCode(ErrorCode.UNKNOWN_ERROR);
			result.setMessage("Event type not supported by this endpoint: " + String.valueOf(eventInfo.getType()));
		}

		return result;
	}

	@Override
	public void notifyAppDirect(RemoteEvent event) {
		try {
			AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
			APIResult result = service.handle(event);
			if (!result.isSuccess()) {
				log.error("Call to AppDirect API failed with message: " + result.getMessage());
			}
		} catch (WebApplicationException e) {
			log.error("Something went wrong while notifying AppDirect.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean isUserExists(String openId, String email) {
		if (openId == null && email == null) {
			throw new IllegalArgumentException("openId and email cannot be both null.");
		}
		DetachedCriteria criteria;
		if (openId == null) {
			criteria = DetachedCriteria.forClass(User.class).add(Property.forName("email").eq(email));
		} else if (email == null) {
			criteria = DetachedCriteria.forClass(User.class).add(Property.forName("openId").eq(openId));
		} else {
			criteria = DetachedCriteria.forClass(User.class).add(Restrictions.or(Property.forName("openId").eq(openId), Property.forName("email").eq(email)));
		}
		List<User> users = userDao.findByCriteria(criteria);
		if (users != null && !users.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
