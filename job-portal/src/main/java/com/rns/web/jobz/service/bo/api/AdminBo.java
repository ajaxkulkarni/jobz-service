package com.rns.web.jobz.service.bo.api;

import java.util.List;

import com.rns.web.jobz.service.bo.domain.AdminMail;
import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;

public interface AdminBo {
	
	List<JobApplication> getAllPostedJobs();
	
	List<JobApplication> getAllPendingJobs();
	
	List<JobApplication> getAllAcceptedJobs();

	List<Candidate> getAllUsers();
	
	String sendToAll(AdminMail mail);
	

}
