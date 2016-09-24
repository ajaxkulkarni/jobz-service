package com.rns.web.jobz.service.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.jobz.service.bo.api.AdminBo;
import com.rns.web.jobz.service.bo.domain.AdminResponse;
import com.rns.web.jobz.service.bo.domain.JobServiceRequest;
import com.rns.web.jobz.service.bo.domain.JobServiceResponse;
import com.rns.web.jobz.service.util.JobzConstants;
import com.rns.web.jobz.service.util.LoggingUtil;

@Component
@Path("/adminService")
public class AdminService implements JobzConstants {

	@Autowired(required = true)
	@Qualifier(value = "adminBo")
	AdminBo adminBo;

	@POST
	@Path("/adminGetAllPostedJobs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse getPostedJobs(JobServiceRequest request) {
		//System.out.println("Here in search skill!");
		LoggingUtil.logObject("Admin posted jobs Request :", request);
		JobServiceResponse response = initResponse();
		try {
			AdminResponse admin = new AdminResponse();
			admin.setPostedJobs(adminBo.getAllPostedJobs());
			response.setAdminResponse(admin);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Admin posted jobs Response :", response);
		return response;
	}
	
	@POST
	@Path("/adminGetAllPendingJobs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse getPendingJobs(JobServiceRequest request) {
		//System.out.println("Here in search skill!");
		LoggingUtil.logObject("Admin pending jobs Request :", request);
		JobServiceResponse response = initResponse();
		try {
			AdminResponse admin = new AdminResponse();
			admin.setPostedJobs(adminBo.getAllPendingJobs());
			response.setAdminResponse(admin);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Admin pending jobs Response :", response);
		return response;
	}
	
	@POST
	@Path("/adminGetAllAcceptedJobs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse getAcceptedJobs(JobServiceRequest request) {
		//System.out.println("Here in search skill!");
		LoggingUtil.logObject("Admin accepted jobs Request :", request);
		JobServiceResponse response = initResponse();
		try {
			AdminResponse admin = new AdminResponse();
			admin.setPostedJobs(adminBo.getAllAcceptedJobs());
			response.setAdminResponse(admin);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Admin accepted jobs Response :", response);
		return response;
	}

	


	private JobServiceResponse initResponse() {
		JobServiceResponse response = new JobServiceResponse();
		response.setResponseText(RESPONSE_OK);
		response.setStatus(200);
		return response;
	}

}