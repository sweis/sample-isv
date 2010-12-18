package com.origonetworks.isv.backend.integration.remote.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.origonetworks.isv.backend.integration.remote.vo.APIResult;
import com.origonetworks.isv.backend.integration.remote.vo.EventInfo;
import com.origonetworks.isv.backend.integration.remote.vo.RemoteEvent;

@Path("api")
public interface AppDirectEventService {
	@GET
	@Path("events/{token}")
	@Produces({ "application/xml", "application/json" })
	public EventInfo readInfo(@PathParam("token") String token);

	@POST
	@Path("events/")
	@Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	public APIResult handle(RemoteEvent event);
}
