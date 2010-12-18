package com.origonetworks.isv.wicket.pages;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.wicket.pages.account.AccountPage;

public class HomePage extends BaseWebPage {
	public HomePage(PageParameters parameters) {
		super(parameters);

		List<AccountBean> accountBeans = SpringApplicationContext.getISVService().readAccounts();
		final DataView<AccountBean> dataView = new DataView<AccountBean>("row", new ListDataProvider<AccountBean>(accountBeans)) {
			private static final long serialVersionUID = 4514620914249737635L;

			@Override
			public void populateItem(final Item<AccountBean> item) {
				PageParameters parameters = new PageParameters();
				parameters.add(AccountPage.ACCOUNT_ID_PARAM, item.getModelObject().getId().toString());
				BookmarkablePageLink<Void> idLink = new BookmarkablePageLink<Void>("idLink", AccountPage.class, parameters);
				idLink.add(new Label("id", new PropertyModel<Long>(item.getModelObject(), "id")));
				item.add(idLink);
				BookmarkablePageLink<Void> identifierLink = new BookmarkablePageLink<Void>("identifierLink", AccountPage.class, parameters);
				identifierLink.add(new Label("identifier", new PropertyModel<String>(item.getModelObject(), "identifier")));
				item.add(identifierLink);
				item.add(new Label("editionCode", new PropertyModel<String>(item.getModelObject(), "editionCode")));
				item.add(new Label("maxUsers", new PropertyModel<Integer>(item.getModelObject(), "maxUsers")));
			}
		};
		add(dataView);
	}
}
