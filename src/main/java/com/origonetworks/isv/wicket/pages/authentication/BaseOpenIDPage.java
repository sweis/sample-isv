package com.origonetworks.isv.wicket.pages.authentication;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.openid.OpenIDConsumerException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.wicket.application.ISVApplication;
import com.origonetworks.isv.wicket.pages.BaseWebPage;

public abstract class BaseOpenIDPage extends BaseWebPage {
	private static final Logger log = Logger.getLogger(BaseOpenIDPage.class);

	public BaseOpenIDPage() {
		super();
	}

	public BaseOpenIDPage(PageParameters parameters) {
		super(parameters);
	}

	protected void beginLogin(WebRequest request, WebResponse response) {
		String claimedIdentity = request.getParameter(OpenIDAuthenticationFilter.DEFAULT_CLAIMED_IDENTITY_FIELD);
		if (claimedIdentity == null) {
			claimedIdentity = SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/openid/id";
		}
		try {
			String returnToUrl = buildReturnToUrl(request);
			String realm = lookupRealm(returnToUrl);
			String openIdLoginPageUrl = ((ISVApplication) getApplication()).getOpenIdConsumer().beginConsumption(request.getHttpServletRequest(), claimedIdentity, returnToUrl, realm);
			if (log.isDebugEnabled()) {
				log.debug("return_to is '" + returnToUrl + "', realm is '" + realm + "'");
				log.debug("Redirecting to " + openIdLoginPageUrl);
			}
			// TODO: Check OpenID version (it's in the session).
			// TODO: Make sure we trust the OpenID provider!!!
			RequestCycle.get().setRequestTarget(new RedirectRequestTarget(openIdLoginPageUrl));
		} catch (OpenIDConsumerException e) {
			log.debug("Failed to consume claimedIdentity: " + claimedIdentity, e);
			throw new AuthenticationServiceException("Unable to process claimed identity '" + claimedIdentity + "'");
		}
	}

	protected OpenIDAuthenticationToken finishLogin(WebRequest request, WebResponse response, String identity) {
		if (log.isDebugEnabled()) {
			log.debug("Supplied OpenID identity is " + identity);
		}

		OpenIDAuthenticationToken token;
		try {
			token = ((ISVApplication) getApplication()).getOpenIdConsumer().endConsumption(request.getHttpServletRequest());
		} catch (OpenIDConsumerException oice) {
			throw new AuthenticationServiceException("Consumer error", oice);
		}

		token.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request.getHttpServletRequest()));

		return token;
	}

	private static String buildReturnToUrl(Request request) {
		String requestedUrl = ((WebRequest) request).getHttpServletRequest().getRequestURL().toString();
		log.info("returnTuUrl = " + requestedUrl);
		return requestedUrl;
	}

	private static String lookupRealm(String returnToUrl) {
		try {
			URL url = new URL(returnToUrl);
			int port = url.getPort();

			StringBuilder realmBuffer = new StringBuilder(returnToUrl.length()).append(url.getProtocol()).append("://").append(url.getHost());
			if (port > 0) {
				realmBuffer.append(":").append(port);
			}
			realmBuffer.append("/");
			return realmBuffer.toString();
		} catch (MalformedURLException e) {
			log.warn("returnToUrl was not a valid URL: [" + returnToUrl + "]", e);
			throw new AuthenticationServiceException("URL parsing error", e);
		}
	}
}
