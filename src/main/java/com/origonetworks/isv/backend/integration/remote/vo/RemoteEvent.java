package com.origonetworks.isv.backend.integration.remote.vo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.origonetworks.isv.backend.integration.remote.type.RemoteEventType;

@XmlRootElement(name = "event")
public class RemoteEvent {
	private RemoteEventType type;
	private RemoteEventPayload payload;

	public RemoteEventType getType() {
		return type;
	}

	public void setType(RemoteEventType type) {
		this.type = type;
	}

	@XmlElement(name = "payload")
	public RemoteEventPayload getPayload() {
		return payload;
	}

	public void setPayload(RemoteEventPayload payload) {
		this.payload = payload;
	}
}
