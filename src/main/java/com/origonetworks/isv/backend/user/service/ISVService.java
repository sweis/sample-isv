package com.origonetworks.isv.backend.user.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.origonetworks.isv.backend.integration.remote.vo.APIResult;
import com.origonetworks.isv.backend.integration.remote.vo.AccountSummary;
import com.origonetworks.isv.backend.integration.remote.vo.RemoteEvent;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;

@Path("service")
public interface ISVService {
	public Long create(AccountBean accountBean, UserBean adminBean);

	public void create(UserBean userBean, AccountBean accountBean);

	@GET
	@Path("users")
	@Produces({ "application/xml", "application/json" })
	public List<UserBean> readUsers();

	@GET
	@Path("users/{userId}")
	@Produces({ "application/xml", "application/json" })
	public UserBean readUser(@PathParam("userId") Long userId);

	public AccountBean readAccount(Long accountId);

	public AccountBean readAccount(UserBean userBean);

	public List<AccountBean> readAccounts();

	@GET
	@Path("accounts/{accountIdentifier}")
	@Produces({ "application/xml", "application/json" })
	public AccountSummary readAccountSummary(@PathParam("accountIdentifier") String accountIdentifier);

	public void update(AccountBean accountBean);

	public void update(UserBean userBean);

	public void delete(AccountBean accountBean);

	public void delete(UserBean userBean);

	@GET
	@Path("events/{token}")
	@Produces({ "application/xml", "application/json" })
	public APIResult processEvent(@PathParam("token") String token);

	public void notifyAppDirect(RemoteEvent event);

	/**
	 * Checks whether a user exists with this OpenID or email.
	 *
	 * @param openId
	 * @param email
	 * @return
	 */
	public boolean isUserExists(String openId, String email);
}
