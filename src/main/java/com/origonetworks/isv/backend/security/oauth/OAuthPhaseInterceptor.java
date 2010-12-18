package com.origonetworks.isv.backend.security.oauth;

import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;

public class OAuthPhaseInterceptor<T extends Message> extends AbstractPhaseInterceptor<T> {
	private static final Logger LOG = Logger.getLogger(OAuthPhaseInterceptor.class);

	private OAuthConsumer consumer;

	public OAuthPhaseInterceptor() {
		super(Phase.SEND);
	}

	@Override
	public void handleMessage(T message) throws Fault {
		if (consumer == null) {
			consumer = new DefaultOAuthConsumer(SpringApplicationContext.getServerConfiguration().getOAuthConsumerKey(), SpringApplicationContext.getServerConfiguration().getOAuthConsumerSecret());
		}
		LOG.debug("Entering handleMessage");
		HttpURLConnection connect = (HttpURLConnection) message.get(HTTPConduit.KEY_HTTP_CONNECTION);
		if (connect == null) {
			return;
		}
		URL url = connect.getURL();
		if (url == null) {
			return;
		}
		LOG.debug(String.format("Request: %s", url.toString()));
		if (url.toString().startsWith(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest")) {
			try {
				consumer.sign(connect);
				LOG.debug(String.format("Request: %s signed", url.toString()));
			} catch (OAuthException e) {
				e.printStackTrace();
			}
		}
	}
}
