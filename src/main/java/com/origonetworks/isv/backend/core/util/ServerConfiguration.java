package com.origonetworks.isv.backend.core.util;

import java.util.Properties;

public class ServerConfiguration {
	private static final String APPDIRECT_BASE_URL = "appdirect.base.url";
	private static final String OAUTH_CONSUMER_KEY = "oauth.consumer.key";
	private static final String OAUTH_CONSUMER_SECRET = "oauth.consumer.secret";

	private Properties properties;

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getAppDirectBaseURL() {
		return properties.getProperty(APPDIRECT_BASE_URL);
	}

	public String getOAuthConsumerKey() {
		return properties.getProperty(OAUTH_CONSUMER_KEY);
	}

	public String getOAuthConsumerSecret() {
		return properties.getProperty(OAUTH_CONSUMER_SECRET);
	}
}
