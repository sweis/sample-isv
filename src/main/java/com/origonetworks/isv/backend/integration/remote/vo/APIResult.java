package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.origonetworks.isv.backend.integration.remote.type.ErrorCode;

@XmlRootElement(name = "result")
public class APIResult implements Serializable {
	private static final long serialVersionUID = -7599199539526987847L;

	private boolean success;
	private ErrorCode errorCode;
	private String message;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
