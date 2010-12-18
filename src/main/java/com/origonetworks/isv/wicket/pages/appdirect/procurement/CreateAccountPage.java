package com.origonetworks.isv.wicket.pages.appdirect.procurement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectEventService;
import com.origonetworks.isv.backend.integration.remote.type.ErrorCode;
import com.origonetworks.isv.backend.integration.remote.type.EventType;
import com.origonetworks.isv.backend.integration.remote.type.PricingUnit;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.integration.remote.vo.OrderInfo;
import com.origonetworks.isv.backend.integration.remote.vo.OrderItemInfo;
import com.origonetworks.isv.backend.security.oauth.OAuthUrlSigner;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.pages.appdirect.AppDirectOpenIDPage;

@MountPath(path = "createaccount")
public class CreateAccountPage extends AppDirectOpenIDPage {
	public static String TOKEN_PARAM = "token";

	public CreateAccountPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void processRequest(String openId, PageParameters parameters) {
		String token = parameters.getString(TOKEN_PARAM);
		AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
		final EventInfo eventInfo = service.readInfo(token);

		if (eventInfo == null || eventInfo.getType() != EventType.SUBSCRIPTION_ORDER) {
			throw new IllegalStateException("Invalid event object.");
		} else if (!openId.equals(eventInfo.getCreator().getOpenId())) {
			throw new IllegalStateException("User not allowed to use this event.");
		}

		if (SpringApplicationContext.getISVService().isUserExists(eventInfo.getCreator().getOpenId(), eventInfo.getCreator().getEmail())) {
			try {
				String errorUrl = eventInfo.getReturnUrl() + "&success=false&errorCode=" + ErrorCode.USER_ALREADY_EXISTS + "&message=" + URLEncoder.encode("An account with this user already exists. Use the Import Account", "UTF-8");
				OAuthUrlSigner.signAndRedirect(getRequestCycle(), errorUrl);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			return;
		}
		add(new Label("openId", eventInfo.getCreator().getOpenId()));
		add(new Label("email", eventInfo.getCreator().getEmail()));
		add(new Label("firstName", eventInfo.getCreator().getFirstName()));
		add(new Label("lastName", eventInfo.getCreator().getLastName()));
		add(new Label("companyName", eventInfo.getPayload().getCompany().getName()));
		add(new Label("companyEmail", eventInfo.getPayload().getCompany().getEmail()));
		add(new Label("companyPhone", eventInfo.getPayload().getCompany().getPhoneNumber()));
		add(new Label("companyWebsite", eventInfo.getPayload().getCompany().getWebsite()));
		add(new Label("editionCode", eventInfo.getPayload().getOrder().getEditionCode()));
		add(new Label("numOfSeats", String.valueOf(getMaxUsers(eventInfo.getPayload().getOrder()))));
		add(new Label("returnUrl", eventInfo.getReturnUrl()));
		add(new Link<Void>("createAccount") {
			private static final long serialVersionUID = 3944839969882170140L;

			@Override
			public void onClick() {
				// Create the account.
				UserBean adminBean = new UserBean();
				adminBean.setOpenId(eventInfo.getCreator().getOpenId());
				adminBean.setEmail(eventInfo.getCreator().getEmail());
				adminBean.setFirstName(eventInfo.getCreator().getFirstName());
				adminBean.setLastName(eventInfo.getCreator().getLastName());
				adminBean.setAdmin(true);
				adminBean.setUsername(adminBean.getFirstName().substring(0, 1).toLowerCase() + adminBean.getLastName().toLowerCase() + "_" + (new Date()).getTime());
				adminBean.setPassword("password");

				AccountBean accountBean = new AccountBean();
				accountBean.setIdentifier(UUID.randomUUID().toString());
				accountBean.setEditionCode(eventInfo.getPayload().getOrder().getEditionCode());
				accountBean.setMaxUsers(getMaxUsers(eventInfo.getPayload().getOrder()));

				SpringApplicationContext.getISVService().create(accountBean, adminBean);
				String successUrl = eventInfo.getReturnUrl() + "&success=true&accountIdentifier=" + accountBean.getIdentifier();
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
