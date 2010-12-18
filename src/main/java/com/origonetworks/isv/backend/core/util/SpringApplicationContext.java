package com.origonetworks.isv.backend.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.origonetworks.isv.backend.user.service.ISVService;

@Component
public class SpringApplicationContext implements ApplicationContextAware {
	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}

	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

	public static ServerConfiguration getServerConfiguration() {
		return (ServerConfiguration) CONTEXT.getBean("serverConfiguration");
	}

	public static ISVService getISVService() {
		return (ISVService) CONTEXT.getBean("isvService");
	}
}
