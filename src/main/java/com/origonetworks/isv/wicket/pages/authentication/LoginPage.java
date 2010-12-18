package com.origonetworks.isv.wicket.pages.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.wicket.pages.BaseWebPage;

@MountPath(path = "login")
public class LoginPage extends BaseWebPage {
	public LoginPage(PageParameters parameters) {
		super(parameters);
		add(new FeedbackPanel("feedback"));
		add(new SignInPanel("signInPanel"));
	}
}
