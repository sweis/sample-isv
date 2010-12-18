package com.origonetworks.isv.wicket.pages.account;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.pages.BaseWebPage;

@MountPath(path = "register")
public class RegisterPage extends BaseWebPage {
	public RegisterPage(PageParameters parameters) {
		super(parameters);
		add(new RegisterForm("registerForm"));
	}

	private static class RegisterForm extends Form<Void> {
		private static final long serialVersionUID = -8069294534860891266L;

		public RegisterForm(String id) {
			super(id);
			final UserBean userBean = new UserBean();
			final AccountBean accountBean = new AccountBean();

			add(new TextField<String>("identifier", new PropertyModel<String>(accountBean, "identifier")));
			add(new TextField<String>("editionCode", new PropertyModel<String>(accountBean, "editionCode")));
			add(new TextField<String>("maxUsers", new PropertyModel<String>(accountBean, "maxUsers")));

			add(new TextField<String>("username", new PropertyModel<String>(userBean, "username")));
			add(new TextField<String>("password", new PropertyModel<String>(userBean, "password")));
			add(new TextField<String>("email", new PropertyModel<String>(userBean, "email")));
			add(new TextField<String>("firstName", new PropertyModel<String>(userBean, "firstName")));
			add(new TextField<String>("lastName", new PropertyModel<String>(userBean, "lastName")));
			add(new TextField<String>("zipCode", new PropertyModel<String>(userBean, "zipCode")));
			add(new TextField<String>("department", new PropertyModel<String>(userBean, "department")));

			add(new AjaxButton("createButton", this) {
				private static final long serialVersionUID = -3505373258148534293L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Long accountId = SpringApplicationContext.getISVService().create(accountBean, userBean);
					PageParameters parameters = new PageParameters();
					parameters.add(AccountPage.ACCOUNT_ID_PARAM, accountId.toString());
					setResponsePage(AccountPage.class, parameters);
					setRedirect(true);
				}
			});
		}
	}
}
