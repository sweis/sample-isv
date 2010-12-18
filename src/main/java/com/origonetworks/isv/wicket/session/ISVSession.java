package com.origonetworks.isv.wicket.session;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.application.ISVApplication;

/**
 * Our custom web session implementation.
 */
public class ISVSession extends AuthenticatedWebSession {
	private static final long serialVersionUID = -3035882552790674791L;

	private static final Logger logger = Logger.getLogger(ISVSession.class);

	private PageParameters openIdPageParams;

	public PageParameters getOpenIDPageParams() {
		return openIdPageParams;
	}

	public void setOpenIDPageParams(PageParameters openIdPageParams) {
		this.openIdPageParams = openIdPageParams;
	}

	public ISVSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String username, String password) {
		boolean authenticated = false;
		try {
			Authentication authentication = ISVApplication.get().getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			authenticated = authentication.isAuthenticated();
		} catch (AuthenticationException e) {
			logger.warn(String.format("User '%s' failed to login. Reason: %s.", username, e.getMessage()));
		} catch (ObjectNotFoundException onfe) {
			logger.warn(String.format("User with username '%s' could not be found.", username));
		}
		return authenticated;
	}

	public void authenticate(OpenIDAuthenticationToken token) {
		if (logger.isDebugEnabled()) {
			logger.debug("Receiving OpenIDAuthenticationToken: " + token.toString());
		}
		boolean isAuthenticated = token.isAuthenticated();
		SecurityContextHolder.getContext().setAuthentication(token);
		signIn(isAuthenticated);
	}

	@Override
	public Roles getRoles() {
		Roles roles = new Roles();
		if (isSignedIn()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				roles.add(authority.getAuthority());
			}
		}
		return roles;
	}

	/**
	 * Returns the current session as an instance of this class.
	 *
	 * @return the current session
	 */
	public static ISVSession get() {
		return (ISVSession) AuthenticatedWebSession.get();
	}

	public UserBean getCurrentUser() {
		if (isSignedIn()) {
			return (UserBean) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} else {
			return null;
		}
	}
}
