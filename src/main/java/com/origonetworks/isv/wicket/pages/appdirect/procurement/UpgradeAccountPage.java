package com.origonetworks.isv.wicket.pages.appdirect.procurement;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectEventService;
import com.origonetworks.isv.backend.integration.remote.type.EventType;
import com.origonetworks.isv.backend.integration.remote.type.PricingUnit;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.integration.remote.vo.OrderInfo;
import com.origonetworks.isv.backend.integration.remote.vo.OrderItemInfo;
import com.origonetworks.isv.backend.security.oauth.OAuthUrlSigner;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.wicket.pages.appdirect.AppDirectOpenIDPage;

@MountPath(path = "upgradeaccount")
public class UpgradeAccountPage extends AppDirectOpenIDPage {
	public static String TOKEN_PARAM = "token";

	public UpgradeAccountPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void processRequest(String openId, PageParameters parameters) {
		String token = parameters.getString(TOKEN_PARAM);
		AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
		final EventInfo eventInfo = service.readInfo(token);

		if (eventInfo == null || eventInfo.getType() != EventType.SUBSCRIPTION_CHANGE) {
			throw new IllegalStateException("Invalid event object.");
		} else if (!openId.equals(eventInfo.getCreator().getOpenId())) {
			throw new IllegalStateException("User not allowed to use this event.");
		}

		add(new Label("accountIdentifier", eventInfo.getPayload().getAccount().getAccountIdentifier()));
		add(new Label("openId", eventInfo.getCreator().getOpenId()));
		add(new Label("firstName", eventInfo.getCreator().getFirstName()));
		add(new Label("lastName", eventInfo.getCreator().getLastName()));
		add(new Label("editionCode", eventInfo.getPayload().getOrder().getEditionCode()));
		add(new Label("numOfSeats", String.valueOf(getMaxUsers(eventInfo.getPayload().getOrder()))));
		add(new Label("returnUrl", eventInfo.getReturnUrl()));
		add(new Link<Void>("upgradeAccount") {
			private static final long serialVersionUID = 3944839969882170140L;

			@Override
			public void onClick() {
				AccountBean accountBean = new AccountBean();
				accountBean.setIdentifier(eventInfo.getPayload().getAccount().getAccountIdentifier());
				accountBean.setEditionCode(eventInfo.getPayload().getOrder().getEditionCode());
				accountBean.setMaxUsers(getMaxUsers(eventInfo.getPayload().getOrder()));

				SpringApplicationContext.getISVService().update(accountBean);
				String successUrl = eventInfo.getReturnUrl() + "&success=true";
				OAuthUrlSigner.signAndRedirect(getRequestCycle(), successUrl);
			}
		});
	}

	private static Integer getMaxUsers(OrderInfo order) {
		Integer maxUsers = null;
		for (OrderItemInfo item : order.getItems()) {
			if (PricingUnit.USER.equals(item.getUnit())) {
				maxUsers = Integer.valueOf(item.getQuantity());
			}
		}
		return maxUsers;
	}
}
