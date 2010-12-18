package com.origonetworks.isv.backend.security.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.signature.QueryStringSigningStrategy;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;

public class OAuthUrlSigner {
	private static final Logger LOG = Logger.getLogger(OAuthUrlSigner.class);
	private static final OAuthConsumer CONSUMER = new DefaultOAuthConsumer(SpringApplicationContext.getServerConfiguration().getOAuthConsumerKey(), SpringApplicationContext.getServerConfiguration().getOAuthConsumerSecret());

	static {
		CONSUMER.setSigningStrategy(new QueryStringSigningStrategy());
	}

	public static String sign(String urlString) {
		try {
			String signedUrl = CONSUMER.sign(urlString);
			return signedUrl;
		} catch (OAuthException e) {
			return urlString;
		}
	}

	public static void signAndRedirect(RequestCycle cycle, String url) {
		String signedUrl = OAuthUrlSigner.sign(url);
		LOG.debug(String.format("SignedUrl : %s", signedUrl));
		cycle.setRequestTarget(new RedirectRequestTarget(signedUrl));
	}
}
