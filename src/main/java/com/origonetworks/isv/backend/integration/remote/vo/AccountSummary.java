package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="account")
public class AccountSummary implements Serializable {
	private static final long serialVersionUID = -8941958436060240097L;

	private String accountIdentifier;
	private String editionCode;
	private Integer maxNumOfUsers;
	private Integer numOfUsers;
	private List<String> syncedOpenIds;

	public String getAccountIdentifier() {
		return accountIdentifier;
	}

	public void setAccountIdentifier(String accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}

	public String getEditionCode() {
		return editionCode;
	}

	public void setEditionCode(String editionCode) {
		this.editionCode = editionCode;
	}

	public Integer getMaxNumOfUsers() {
		return maxNumOfUsers;
	}

	public void setMaxNumOfUsers(Integer maxNumOfUsers) {
		this.maxNumOfUsers = maxNumOfUsers;
	}

	public Integer getNumOfUsers() {
		return numOfUsers;
	}

	public void setNumOfUsers(Integer numOfUsers) {
		this.numOfUsers = numOfUsers;
	}

	@XmlElement(name = "userOpenId")
	public List<String> getSyncedOpenIds() {
		return syncedOpenIds;
	}

	public void setSyncedOpenIds(List<String> syncedOpenIds) {
		this.syncedOpenIds = syncedOpenIds;
	}
}
