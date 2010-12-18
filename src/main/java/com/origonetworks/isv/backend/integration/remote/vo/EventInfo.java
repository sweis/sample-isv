package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.origonetworks.isv.backend.integration.remote.type.EventType;

@XmlRootElement(name = "event")
public class EventInfo implements Serializable {
	private static final long serialVersionUID = 2658400228024450854L;

	private EventType type;
	private UserInfo creator;
	private EventPayload payload;
	private String returnUrl;

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	@XmlElement(name = "creator")
	public UserInfo getCreator() {
		return creator;
	}

	public void setCreator(UserInfo creator) {
		this.creator = creator;
	}

	@XmlElement(name = "payload")
	public EventPayload getPayload() {
		return payload;
	}

	public void setPayload(EventPayload payload) {
		this.payload = payload;
	}

	@XmlElement(name = "returnUrl")
	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}
