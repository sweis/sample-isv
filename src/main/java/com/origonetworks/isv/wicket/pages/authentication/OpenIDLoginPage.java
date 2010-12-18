package com.origonetworks.isv.wicket.pages.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.util.StringUtils;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.wicket.application.ISVApplication;
import com.origonetworks.isv.wicket.session.ISVSession;

@MountPath(path = "openidlogin")
public class OpenIDLoginPage extends BaseOpenIDPage {
	private static final Logger log = Logger.getLogger(OpenIDLoginPage.class);

	public OpenIDLoginPage() {
		super();
		WebRequest request = getWebRequestCycle().getWebRequest();
		WebResponse response = getWebRequestCycle().getWebResponse();
		String identity = request.getParameter("openid.identity");
		if (!StringUtils.hasText(identity)) {
			beginLogin(request, response);
		} else {
			OpenIDAuthenticationToken token = finishLogin(request, response, identity);
			try {
				// Delegate to the authentication provider.
				OpenIDAuthenticationToken authentication = (OpenIDAuthenticationToken) ((ISVApplication) getApplication()).getOpenIdAuthenticationProvider().authenticate(token);
				if (authentication == null || !authentication.isAuthenticated()) {
					log.warn("The OpenID authentication failed.");
					error("The OpenID authentication failed.");
				} else {
					ISVSession.get().authenticate(authentication);
				}

				// TODO: Redirect somewhere more relevant? Check Component#continueToOriginalDestination()?
				setResponsePage(Application.get().getHomePage());
				setRedirect(true);
			} catch (ObjectNotFoundException e) {
				getSession().error("Unknown user with OpenID = " + e.getIdentifier());
				setResponsePage(LoginPage.class);
				setRedirect(true);
			}
		}
	}
}
