package com.origonetworks.isv.wicket.pages.appdirect.procurement;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.hibernate.ObjectNotFoundException;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectEventService;
import com.origonetworks.isv.backend.integration.remote.type.ErrorCode;
import com.origonetworks.isv.backend.integration.remote.type.EventType;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.security.oauth.OAuthUrlSigner;
import com.origonetworks.isv.backend.user.vo.AccountBean;
import com.origonetworks.isv.wicket.pages.appdirect.AppDirectOpenIDPage;

@MountPath(path = "cancelaccount")
public class CancelAccountPage extends AppDirectOpenIDPage {
	public static String TOKEN_PARAM = "token";

	public CancelAccountPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void processRequest(String openId, PageParameters parameters) {
		String token = parameters.getString(TOKEN_PARAM);
		AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
		final EventInfo eventInfo = service.readInfo(token);

		if (eventInfo == null || eventInfo.getType() != EventType.SUBSCRIPTION_CANCEL) {
			throw new IllegalStateException("Invalid event object.");
		} else if (!openId.equals(eventInfo.getCreator().getOpenId())) {
			throw new IllegalStateException("User not allowed to use this event.");
		}

		add(new Label("openId", eventInfo.getCreator().getOpenId()));
		add(new Label("firstName", eventInfo.getCreator().getFirstName()));
		add(new Label("lastName", eventInfo.getCreator().getLastName()));
		add(new Label("accountIdentifier", eventInfo.getPayload().getAccount().getAccountIdentifier()));
		add(new Label("returnUrl", eventInfo.getReturnUrl()));
		add(new Link<Void>("cancelAccount") {
			private static final long serialVersionUID = -9095275432565219418L;

			@Override
			public void onClick() {
				// Delete the account.
				try {
					AccountBean accountBean = new AccountBean();
					accountBean.setIdentifier(eventInfo.getPayload().getAccount().getAccountIdentifier());
					SpringApplicationContext.getISVService().delete(accountBean);
					String successUrl = eventInfo.getReturnUrl() + "&success=true";
					OAuthUrlSigner.signAndRedirect(getRequestCycle(), successUrl);
				} catch (ObjectNotFoundException e) {
					String successUrl = eventInfo.getReturnUrl() + "&success=false&errorCode=" + ErrorCode.ACCOUNT_NOT_FOUND;
					OAuthUrlSigner.signAndRedirect(getRequestCycle(), successUrl);
				}
			}
		});
	}
}
