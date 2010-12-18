package com.origonetworks.isv.backend.integration.remote.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.origonetworks.isv.backend.integration.remote.vo.BillingAPIResult;
import com.origonetworks.isv.backend.integration.remote.vo.UsageBean;

@Path("billing")
public interface AppDirectBillingService {
	@POST
	@Path("usage")
	@Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	public BillingAPIResult billUsage(UsageBean usageBean);
}
