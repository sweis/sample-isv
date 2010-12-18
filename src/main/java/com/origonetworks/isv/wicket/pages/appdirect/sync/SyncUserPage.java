package com.origonetworks.isv.wicket.pages.appdirect.sync;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.wicketstuff.annotation.mount.MountPath;

import com.origonetworks.isv.backend.core.util.SpringApplicationContext;
import com.origonetworks.isv.backend.integration.remote.service.AppDirectEventService;
import com.origonetworks.isv.backend.integration.remote.type.ErrorCode;
import com.origonetworks.isv.backend.integration.remote.type.EventType;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.security.oauth.OAuthUrlSigner;
import com.origonetworks.isv.backend.user.vo.UserBean;
import com.origonetworks.isv.wicket.pages.appdirect.AppDirectOpenIDPage;
import com.origonetworks.isv.wicket.session.ISVSession;

@AuthorizeInstantiation({ "USER" })
@MountPath(path = "syncuser")
public class SyncUserPage extends AppDirectOpenIDPage {
	public static String TOKEN_PARAM = "token";

	public SyncUserPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void processRequest(final String openId, PageParameters parameters) {
		String token = parameters.getString(TOKEN_PARAM);
		AppDirectEventService service = JAXRSClientFactory.create(SpringApplicationContext.getServerConfiguration().getAppDirectBaseURL() + "/rest", AppDirectEventService.class);
		final EventInfo eventInfo = service.readInfo(token);

		final UserBean userBean = ISVSession.get().getCurrentUser();

		if (eventInfo == null || eventInfo.getType() != EventType.USER_SYNC) {
			throw new IllegalStateException("Invalid event object.");
		} else if (!openId.equals(eventInfo.getPayload().getUser().getOpenId())) {
			throw new IllegalStateException("User not allowed to use this event.");
		} else if (!SpringApplicationContext.getISVService().readAccount(userBean).getIdentifier().equals(eventInfo.getPayload().getAccount().getAccountIdentifier())) {
			try {
				String redirectUrl = eventInfo.getReturnUrl() + "&success=false&errorCode=" + ErrorCode.UNAUTHORIZED +"&message=" + URLEncoder.encode("Account identifiers do not match.", "UTF-8");
				OAuthUrlSigner.signAndRedirect(getRequestCycle(), redirectUrl);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 is not available.", e);
			}
		}

		add(new Label("openid", new Model<String>(openId)));
		add(new Label("email", new Model<String>(eventInfo.getPayload().getUser().getEmail())));
		add(new Label("name", new Model<String>(eventInfo.getPayload().getUser().getFirstName() + " " + eventInfo.getPayload().getUser().getLastName())));
		add(new Label("returnUrl", new Model<String>(eventInfo.getReturnUrl())));

		add(new AjaxFallbackLink<Void>("connect") {
			private static final long serialVersionUID = -5591830666827928533L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				userBean.setOpenId(openId);
				SpringApplicationContext.getISVService().update(userBean);
				String redirectUrl = eventInfo.getReturnUrl() + "&success=true";
				OAuthUrlSigner.signAndRedirect(getRequestCycle(), redirectUrl);
			}
		});

		add(new AjaxFallbackLink<Void>("cancel") {
			private static final long serialVersionUID = -5591830666827928533L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					String redirectUrl = eventInfo.getReturnUrl() + "&success=false&errorCode=" + ErrorCode.OPERATION_CANCELLED +"&message=" + URLEncoder.encode("User refused to link his account.", "UTF-8");
					OAuthUrlSigner.signAndRedirect(getRequestCycle(), redirectUrl);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("UTF-8 is not available.", e);
				}
			}
		});
	}
}
