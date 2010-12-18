package com.origonetworks.isv.backend.integration.remote.type;

public enum ErrorCode {
	USER_ALREADY_EXISTS,
	USER_NOT_FOUND,
	ACCOUNT_NOT_FOUND,
	MAX_USERS_REACHED,
	UNAUTHORIZED,
	OPERATION_CANCELLED,
	UNKNOWN_ERROR;

	public static ErrorCode fromString(String string) {
		for (ErrorCode errorCode: values()) {
			if (errorCode.toString().equals(string)) {
				return errorCode;
			}
		}
		return null;
	}
}
