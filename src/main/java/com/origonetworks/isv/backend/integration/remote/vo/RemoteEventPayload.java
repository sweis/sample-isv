package com.origonetworks.isv.backend.integration.remote.vo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "payload")
public class RemoteEventPayload {
	private AccountInfo account;

	@XmlElement(name = "account")
	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}
}
