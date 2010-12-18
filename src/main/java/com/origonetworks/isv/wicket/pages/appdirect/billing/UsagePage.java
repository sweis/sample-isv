package com.origonetworks.isv.wicket.pages.appdirect.billing;

import java.util.Arrays;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectBillingService;
import com.origonetworks.isv.backend.integration.remote.type.PricingUnit;
import com.origonetworks.isv.backend.integration.remote.vo.AccountInfo;
import com.origonetworks.isv.backend.integration.remote.vo.BillingAPIResult;
import com.origonetworks.isv.backend.integration.remote.vo.UsageBean;
import com.origonetworks.isv.backend.integration.remote.vo.UsageItemBean;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.wicket.pages.BaseWebPage;
import com.origonetworks.isv.wicket.session.ISVSession;

@AuthorizeInstantiation("USER")
@MountPath(path = "usage")
public class UsagePage extends BaseWebPage {
	private static Logger log = Logger.getLogger(UsagePage.class);

	public UsagePage(PageParameters parameters) {
		super(parameters);
		final UsageBean usageBean = new UsageBean();
		AccountBean accountBean = SpringApplicationContext.getISVService().readAccount(ISVSession.get().getCurrentUser());
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setAccountIdentifier(accountBean.getIdentifier());
		usageBean.setAccount(accountInfo);
		UsageItemBean usageItemBean = new UsageItemBean();
		usageBean.getItems().add(usageItemBean);
		Form<Void> usageForm = new Form<Void>("usageForm");
		usageForm.add(new Label("accountIdentifier", new PropertyModel<UsageBean>(usageBean, "account.accountIdentifier")));
		usageForm.add(new TextArea<Integer>("usageQuantity", new PropertyModel<Integer>(usageItemBean, "quantity")));
		usageForm.add(new DropDownChoice<PricingUnit>("usageUnit", new PropertyModel<PricingUnit>(usageItemBean, "unit"), Arrays.asList(PricingUnit.values())));
		usageForm.add(new AjaxButton("report") {
			private static final long serialVersionUID = -5534177485294277213L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AppDirectBillingService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectBillingService.class);
				BillingAPIResult result = service.billUsage(usageBean);
				log.info("Success: " + String.valueOf(result.isSuccess()));
				log.info("Message: " + result.getMessage());
			}
		});
		add(usageForm);
	}
}
