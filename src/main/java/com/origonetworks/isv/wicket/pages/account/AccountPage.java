package com.origonetworks.isv.wicket.pages.account;

import java.util.Arrays;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountMixedParam;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.type.RemoteEventType;
import com.origonetworks.isv.backend.integration.remote.vo.AccountInfo;
import com.origonetworks.isv.backend.integration.remote.vo.RemoteEvent;
import com.origonetworks.isv.backend.integration.remote.vo.RemoteEventPayload;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.application.ISVApplication;
import com.origonetworks.isv.wicket.pages.BaseWebPage;

@MountPath(path = "accounts")
@MountMixedParam(parameterNames = { AccountPage.ACCOUNT_ID_PARAM })
public class AccountPage extends BaseWebPage {
	public static final String ACCOUNT_ID_PARAM = "accountId";

	public AccountPage(final PageParameters parameters) {
		super(parameters);
		Long accountId = parameters.getAsLong(ACCOUNT_ID_PARAM);
		final AccountBean accountBean = SpringApplicationContext.getISVService().readAccount(accountId);
		add(new Label("id", new PropertyModel<Long>(accountBean, "id")));
		add(new Label("identifier", new PropertyModel<String>(accountBean, "identifier")));
		add(new Label("editionCode", new PropertyModel<String>(accountBean, "editionCode")));
		add(new Label("maxUsers", new PropertyModel<Integer>(accountBean, "maxUsers")));
		add(new AjaxFallbackLink<Void>("deleteAccountLink") {
			private static final long serialVersionUID = -278573087265133504L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				SpringApplicationContext.getISVService().delete(accountBean);
				setResponsePage(ISVApplication.get().getHomePage());
				setRedirect(true);
			}
		});
		final DataView<UserBean> dataView = new DataView<UserBean>("row", new ListDataProvider<UserBean>(accountBean.getUsers())) {
			private static final long serialVersionUID = 4514620914249737635L;

			@Override
			public void populateItem(final Item<UserBean> item) {
				item.add(new Label("id", new PropertyModel<Long>(item.getModelObject(), "id")));
				item.add(new Label("username", new PropertyModel<String>(item.getModelObject(), "username")));
				item.add(new Label("password", new PropertyModel<String>(item.getModelObject(), "password")));
				item.add(new Label("openId", new PropertyModel<String>(item.getModelObject(), "openId")));
				item.add(new Label("email", new PropertyModel<String>(item.getModelObject(), "email")));
				item.add(new Label("firstName", new PropertyModel<String>(item.getModelObject(), "firstName")));
				item.add(new Label("lastName", new PropertyModel<String>(item.getModelObject(), "lastName")));
				item.add(new Label("zipCode", new PropertyModel<String>(item.getModelObject(), "zipCode")));
				item.add(new Label("department", new PropertyModel<String>(item.getModelObject(), "department")));
				item.add(new Label("isAdmin", new PropertyModel<Boolean>(item.getModelObject(), "isAdmin")));
				item.add(new AjaxFallbackLink<Void>("deleteLink") {
					private static final long serialVersionUID = -278573087265133504L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						SpringApplicationContext.getISVService().delete(item.getModelObject());
						notifyAppDirect(accountBean);
						setResponsePage(AccountPage.class, parameters);
						setRedirect(true);
					}
				});
			}
		};
		add(dataView);
		add(new AddUserForm("addUserForm", accountBean));
	}

	private static class AddUserForm extends Form<Void> {
		private static final long serialVersionUID = -8069294534860891266L;

		public AddUserForm(String id, final AccountBean accountBean) {
			super(id);
			final UserBean userBean = new UserBean();

			add(new TextField<String>("username", new PropertyModel<String>(userBean, "username")));
			add(new TextField<String>("password", new PropertyModel<String>(userBean, "password")));
			add(new TextField<String>("email", new PropertyModel<String>(userBean, "email")));
			add(new TextField<String>("firstName", new PropertyModel<String>(userBean, "firstName")));
			add(new TextField<String>("lastName", new PropertyModel<String>(userBean, "lastName")));
			add(new TextField<String>("zipCode", new PropertyModel<String>(userBean, "zipCode")));
			add(new TextField<String>("department", new PropertyModel<String>(userBean, "department")));
			add(new DropDownChoice<Boolean>("isAdmin", new PropertyModel<Boolean>(userBean, "isAdmin"), Arrays.asList(Boolean.TRUE, Boolean.FALSE)));

			add(new AjaxButton("addUserButton", this) {
				private static final long serialVersionUID = -3505373258148534293L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					SpringApplicationContext.getISVService().create(userBean, accountBean);
					notifyAppDirect(accountBean);
					PageParameters parameters = new PageParameters();
					parameters.add(AccountPage.ACCOUNT_ID_PARAM, accountBean.getId().toString());
					setResponsePage(AccountPage.class, parameters);
					setRedirect(true);
				}
			});
		}
	}

	private static void notifyAppDirect(AccountBean accountBean) {
		RemoteEvent event = new RemoteEvent();
		event.setType(RemoteEventType.USER_LIST_CHANGE);
		RemoteEventPayload payload = new RemoteEventPayload();
		AccountInfo account = new AccountInfo();
		account.setAccountIdentifier(accountBean.getIdentifier());
		payload.setAccount(account);
		event.setPayload(payload);
		SpringApplicationContext.getISVService().notifyAppDirect(event);
	}
}
