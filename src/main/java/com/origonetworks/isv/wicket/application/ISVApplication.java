package com.origonetworks.isv.wicket.application;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.openid.OpenIDAuthenticationProvider;
import org.springframework.security.openid.OpenIDConsumer;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import com.origonetworks.isv.wicket.pages.HomePage;
import com.origonetworks.isv.wicket.pages.authentication.LoginPage;
import com.origonetworks.isv.wicket.session.ISVSession;

/**
 * Application object for your web application.
 */
@Component
public class ISVApplication extends AuthenticatedWebApplication {
	protected OpenIDConsumer openIdConsumer;
	protected OpenIDAuthenticationProvider openIdAuthenticationProvider;
	protected AuthenticationManager authenticationManager;

	public OpenIDConsumer getOpenIdConsumer() {
		return openIdConsumer;
	}

	@Autowired
	public void setOpenIdConsumer(OpenIDConsumer openIdConsumer) {
		this.openIdConsumer = openIdConsumer;
	}

	public OpenIDAuthenticationProvider getOpenIdAuthenticationProvider() {
		return openIdAuthenticationProvider;
	}

	@Autowired
	public void setOpenIdAuthenticationProvider(OpenIDAuthenticationProvider openIdAuthenticationProvider) {
		this.openIdAuthenticationProvider = openIdAuthenticationProvider;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public static ISVApplication get() {
		return (ISVApplication) Application.get();
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}

	@Override
	public Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return ISVSession.class;
	}

	@Override
	protected void init() {
		super.init();
		new AnnotatedMountScanner().scanPackage("com.origonetworks.isv").mount(this);
	}
}