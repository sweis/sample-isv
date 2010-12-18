package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usage")
public class UsageBean implements Serializable {
	private static final long serialVersionUID = -227136513522373050L;

	private AccountInfo account;
	private List<UsageItemBean> items = new ArrayList<UsageItemBean>();

	@XmlElement(name = "account")
	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}

	@XmlElement(name = "item")
	public List<UsageItemBean> getItems() {
		return items;
	}

	public void setItems(List<UsageItemBean> items) {
		this.items = items;
	}
}
