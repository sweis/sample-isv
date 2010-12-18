package com.origonetworks.isv.backend.user.util;

import com.origonetworks.isv.backend.user.model.Account;
import com.origonetworks.isv.backend.user.model.User;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;

public final class BeanUtils {
	public static final UserBean createBean(User user) {
		if (user == null) {
			return null;
		}
		UserBean userBean = new UserBean();
		userBean.setId(user.getId());
		userBean.setUsername(user.getUsername());
		userBean.setPassword(user.getPassword());
		userBean.setOpenId(user.getOpenId());
		userBean.setEmail(user.getEmail());
		userBean.setFirstName(user.getFirstName());
		userBean.setLastName(user.getLastName());
		userBean.setZipCode(user.getZipCode());
		userBean.setDepartment(user.getDepartment());
		userBean.setAdmin(user.isAdmin());
		return userBean;
	}

	public static final AccountBean createBean(Account account) {
		if (account == null) {
			return null;
		}
		AccountBean accountBean = new AccountBean();
		accountBean.setId(account.getId());
		accountBean.setIdentifier(account.getIdentifier());
		for (User user : account.getUsers()) {
			accountBean.getUsers().add(createBean(user));
		}
		accountBean.setEditionCode(account.getEditionCode());
		accountBean.setMaxUsers(account.getMaxUsers());
		return accountBean;
	}

	public static final User createPojo(UserBean userBean) {
		User user = new User();
		populateAttributes(userBean, user);
		return user;
	}

	public static Account createPojo(AccountBean accountBean) {
		Account account = new Account();
		account.setIdentifier(accountBean.getIdentifier());
		account.setEditionCode(accountBean.getEditionCode());
		account.setMaxUsers(accountBean.getMaxUsers());
		return account;
	}

	public static void populateAttributes(UserBean userBean, User user) {
		user.setUsername(userBean.getUsername());
		user.setPassword(userBean.getPassword());
		user.setOpenId(userBean.getOpenId());
		user.setEmail(userBean.getEmail());
		user.setFirstName(userBean.getFirstName());
		user.setLastName(userBean.getLastName());
		user.setZipCode(userBean.getZipCode());
		user.setTimezone(userBean.getTimezone());
		user.setDepartment(userBean.getDepartment());
		user.setAdmin(userBean.isAdmin());
	}

	public static void populateAttributes(AccountBean accountBean, Account account) {
		account.setEditionCode(accountBean.getEditionCode());
		account.setMaxUsers(accountBean.getMaxUsers());
	}
}
