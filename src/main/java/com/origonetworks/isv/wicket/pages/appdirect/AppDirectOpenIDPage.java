package com.origonetworks.isv.wicket.pages.appdirect;

import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.util.StringUtils;

import com.origonetworks.isv.wicket.pages.authentication.BaseOpenIDPage;
import com.origonetworks.isv.wicket.session.ISVSession;

public abstract class AppDirectOpenIDPage extends BaseOpenIDPage {
	public AppDirectOpenIDPage(PageParameters parameters) {
		super(parameters);
		WebRequest request = getWebRequestCycle().getWebRequest();
		WebResponse response = getWebRequestCycle().getWebResponse();
		String identity = request.getParameter("openid.identity");
		if (!StringUtils.hasText(identity)) {
			// Save parameters for later.
			ISVSession.get().setOpenIDPageParams(parameters);
			beginLogin(request, response);
		} else {
			OpenIDAuthenticationToken token = finishLogin(request, response, identity);
			if (token.getStatus() != OpenIDAuthenticationStatus.SUCCESS) {
				// TODO: What do we do?
				throw new RuntimeException("OpenID authentication failed.");
			}
			processRequest(token.getIdentityUrl(), ISVSession.get().getOpenIDPageParams());
		}
	}

	protected abstract void processRequest(String openId, PageParameters parameters);
}
