package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;

public class EventPayload implements Serializable {
	private static final long serialVersionUID = 3080925569209286979L;

	private UserInfo user;
	private CompanyInfo company;
	private AccountInfo account;
	private OrderInfo order;

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public CompanyInfo getCompany() {
		return company;
	}

	public void setCompany(CompanyInfo companyInfo) {
		this.company = companyInfo;
	}

	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}

	public OrderInfo getOrder() {
		return order;
	}

	public void setOrder(OrderInfo order) {
		this.order = order;
	}
}
