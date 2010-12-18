package com.origonetworks.isv.wicket.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.application.ISVApplication;
import com.origonetworks.isv.wicket.pages.account.AccountPage;
import com.origonetworks.isv.wicket.pages.account.RegisterPage;
import com.origonetworks.isv.wicket.pages.account.UserListPage;
import com.origonetworks.isv.wicket.pages.authentication.LoginPage;
import com.origonetworks.isv.wicket.pages.authentication.LogoutPage;
import com.origonetworks.isv.wicket.session.ISVSession;

public abstract class BaseWebPage extends WebPage {
	public BaseWebPage() {
		super();
		init();
	}

	public BaseWebPage(PageParameters parameters) {
		super(parameters);
		init();
	}

	private void init() {
		UserBean currentUser = ISVSession.get().getCurrentUser();
		AccountBean currentAccount = currentUser == null ? null : SpringApplicationContext.getISVService().readAccount(currentUser);
		PageParameters accountPageParameters = new PageParameters();
		accountPageParameters.add(AccountPage.ACCOUNT_ID_PARAM, currentAccount == null ? null : currentAccount.getId().toString());

		add(new BookmarkablePageLink<Void>("home", ISVApplication.get().getHomePage()));
		add(new BookmarkablePageLink<Void>("account", AccountPage.class, accountPageParameters).setVisible(currentUser != null));
		add(new BookmarkablePageLink<Void>("register", RegisterPage.class));
		add(new BookmarkablePageLink<Void>("userList", UserListPage.class));
		add(new BookmarkablePageLink<Void>("login", LoginPage.class).setVisible(currentUser == null));
		add(new BookmarkablePageLink<Void>("logout", LogoutPage.class).setVisible(currentUser != null));

		WebMarkupContainer loginInfo = new WebMarkupContainer("loginInfo");
		if (currentUser != null) {
			loginInfo.add(new Label("openId", String.valueOf(currentUser.getOpenId())));
			loginInfo.add(new Label("firstName", currentUser.getFirstName()));
			loginInfo.add(new Label("lastName", currentUser.getLastName()));
		} else {
			loginInfo.setVisible(false);
		}
		add(loginInfo);
	}
}
