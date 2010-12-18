package com.origonetworks.isv.wicket.pages.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.wicket.pages.BaseWebPage;
import com.origonetworks.isv.wicket.session.ISVSession;

@MountPath(path = "logout")
public class LogoutPage extends BaseWebPage {
	public LogoutPage(PageParameters parameters) {
		super(parameters);
		ISVSession.get().signOut();
		ISVSession.get().invalidate();
		RequestCycle.get().setResponsePage(getApplication().getHomePage());
		RequestCycle.get().setRedirect(true);
	}
}
