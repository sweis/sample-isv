package com.origonetworks.isv.wicket.pages.account;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.pages.BaseWebPage;

@MountPath(path = "userlist")
public class UserListPage extends BaseWebPage {
	public UserListPage(PageParameters parameters) {
		super(parameters);
		List<UserBean> userBeans = SpringApplicationContext.getISVService().readUsers();
		final DataView<UserBean> dataView = new DataView<UserBean>("row", new ListDataProvider<UserBean>(userBeans)) {
			private static final long serialVersionUID = -1432601596719602292L;

			@Override
			public void populateItem(final Item<UserBean> item) {
				final UserBean userBean = item.getModelObject();
				item.add(new Label("userId", String.valueOf(userBean.getId())));
				item.add(new Label("username", userBean.getUsername()));
				item.add(new Label("password", userBean.getPassword()));
				item.add(new Label("openId", userBean.getOpenId()));
				item.add(new Label("email", userBean.getEmail()));
				item.add(new Label("firstName", userBean.getFirstName()));
				item.add(new Label("lastName", userBean.getLastName()));
				item.add(new Label("zipCode", userBean.getZipCode()));
				item.add(new Label("department", userBean.getDepartment()));
				item.add(new Label("isAdmin", String.valueOf(userBean.isAdmin())));
			}
		};
		add(dataView);
	}
}
