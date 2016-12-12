package com.rns.web.jobz.service.controller;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.rns.web.jobz.service.bo.api.CandidateBo;
import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobServiceRequest;
import com.rns.web.jobz.service.bo.domain.JobServiceResponse;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;
import com.rns.web.jobz.service.util.CommonUtils;
import com.rns.web.jobz.service.util.JobzConstants;
import com.rns.web.jobz.service.util.LoggingUtil;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Component
@Path("/service")
public class UserService implements JobzConstants {

	@Autowired(required = true)
	@Qualifier(value = "candidateBo")
	CandidateBo candidateBo;

	@POST
	@Path("/searchSkill")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse searchSkill(JobServiceRequest request) {
		System.out.println("Here in search skill!");
		LoggingUtil.logObject("Search Skill Request :", request);
		JobServiceResponse response = initResponse();
		JobSkill skill = new JobSkill();
		skill.setName(request.getJobSkillRequested());
		try {
			response.setMatchingJobSkills(candidateBo.getAvailableSkills(skill));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Search Skill Response :", response);
		return response;
	}

	@POST
	@Path("/searchEducation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse searchEducation(JobServiceRequest request) {
		System.out.println("Here in search education!");
		LoggingUtil.logObject("Search Education Request :", request);
		JobServiceResponse response = initResponse();
		Qualification qualification = new Qualification();
		qualification.setName(request.getEducationRequested());
		try {
			response.setMatchingEducations(candidateBo.getAvailableQualifications(qualification));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Search Education Response :", response);
		return response;
	}

	@POST
	@Path("/registerUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse registerUser(JobServiceRequest request) {
		System.out.println("Here in registration!");
		LoggingUtil.logObject("Register Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.registerCandidate(request.getRequestedBy()));
			CommonUtils.checkForError(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Register Response :", response);
		return response;
	}
	
	@POST
	@Path("/activateUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse activateUser(JobServiceRequest request) {
		System.out.println("Here in activation!");
		LoggingUtil.logObject("Activate Request :", request);
		JobServiceResponse response = initResponse();
		try {
			Candidate activeCandidate = candidateBo.activateCandidate(request.getRequestedBy());
			response.setCandidateProfile(activeCandidate);
			if (activeCandidate == null) {
				response.setResponseText(ERROR_INVALID_ACTIVATION_CODE);
				response.setStatus(-111);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Activate Response :", response);
		return response;
	}

	@POST
	@Path("/loginUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse loginUser(JobServiceRequest request) {
		System.out.println("Here in Login!");
		LoggingUtil.logObject("Login Request :", request);
		JobServiceResponse response = initResponse();
		try {
			Candidate loginCandidate = candidateBo.login(request.getRequestedBy());
			response.setCandidateProfile(loginCandidate);
			if (loginCandidate == null) {
				response.setResponseText(ERROR_INVALID_LOGIN_DETAILS);
				response.setStatus(-111);
			}
			// CommonUtils.checkForError(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Login Response :", response);
		return response;
	}


	@POST
	@Path("/updateUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse updateUser(JobServiceRequest request) {
		System.out.println("Here in update candidate!");
		LoggingUtil.logObject("Update Profile Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.updateCandidate(request.getRequestedBy()));
			CommonUtils.checkForError(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update Profile Response :", response);
		return response;
	}

	@POST
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse populateUser(JobServiceRequest request) {
		System.out.println("Here in populate!");
		LoggingUtil.logObject("Populate Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setCandidateProfile(candidateBo.populateCandidate(request.getRequestedBy()));

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Populate Response :", response);
		return response;
	}

	@POST
	@Path("/postJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse postJob(JobServiceRequest request) {
		System.out.println("Here in post!");
		LoggingUtil.logObject("Post Job Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.postNewJob(request.getPostJobRequested()));
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Post Job Response :", response);
		return response;
	}

	@POST
	@Path("/applyForJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse applyForJob(JobServiceRequest request) {
		System.out.println("Here in apply!");
		LoggingUtil.logObject("Apply Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.applyForJob(request.getApplyJobRequested()));
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Apply Response :", response);
		return response;
	}
	
	@POST
	@Path("/updateJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse updateJob(JobServiceRequest request) {
		System.out.println("Here in update job!");
		LoggingUtil.logObject("Update Job Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.updateJob(request.getPostJobRequested()));
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Update Job Response :", response);
		return response;
	}
	
	@POST
	@Path("/deleteJob")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse deleteJob(JobServiceRequest request) {
		System.out.println("Here in delete job!");
		LoggingUtil.logObject("Delete Job Request :", request);
		JobServiceResponse response = initResponse();
		try {
			response.setResponseText(candidateBo.deleteJob(request.getPostJobRequested()));
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Delete Job Response :", response);
		return response;
	}
	
	@POST
	@Path("/forgotPassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JobServiceResponse forgotPassword(JobServiceRequest request) {
		System.out.println("Here in forgot password!");
		LoggingUtil.logObject("Forgot Pwd Request :", request);
		JobServiceResponse response = initResponse();
		try {
			String result = candidateBo.forgotPassword(request.getRequestedBy());
			response.setResponseText(result);
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Forgot Pwd Response :", response);
		return response;
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public JobServiceResponse uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("email") String email) {

		LoggingUtil.logMessage("Upload CV request for :" + email);
		JobServiceResponse response = initResponse();
		try {
			Candidate candidate = new Candidate();
			candidate.setFilePath(fileDetail.getFileName());
			candidate.setEmail(email);
			candidate.setFile(uploadedInputStream);
			String result = candidateBo.uploadResume(candidate);
			response.setResponseText(result);
			CommonUtils.checkForError(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(-999);
			response.setResponseText(ERROR_IN_PROCESSING);
		}
		LoggingUtil.logObject("Upload resume Response :", response);
		return response;

	}
	
	@GET
	@Path("/download/{email}")
	//@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@PathParam("email") String email) {
		LoggingUtil.logMessage("Download CV request for :" + email);
		try {
			Candidate candidate = new Candidate();
			candidate.setEmail(email);
			Candidate currentCandidate = candidateBo.downloadResume(candidate);
			ResponseBuilder response = Response.ok(currentCandidate.getResume());
			response.header("Content-Disposition","attachment; filename=\"" + currentCandidate.getFilePath() + "\"");  
			return response.build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		//LoggingUtil.logObject("Download resume Response :", response);
		return null;

	}


	private JobServiceResponse initResponse() {
		JobServiceResponse response = new JobServiceResponse();
		response.setResponseText(RESPONSE_OK);
		response.setStatus(200);
		return response;
	}

}