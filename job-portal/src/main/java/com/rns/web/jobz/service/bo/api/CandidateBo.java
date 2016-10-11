package com.rns.web.jobz.service.bo.api;

import java.util.List;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;

public interface CandidateBo {

	String registerCandidate(Candidate candidate);
	String activateCandidate(Candidate candidate);
	String updateCandidate(Candidate candidate);
	Candidate login(Candidate candidate);
	String postNewJob(JobApplication application);
	String applyForJob(JobApplication application);
	Candidate populateCandidate(Candidate candidate);
	List<JobSkill> getAvailableSkills(JobSkill skill);
	List<Qualification> getAvailableQualifications(Qualification qualification);
	String updateJob(JobApplication application);
	String deleteJob(JobApplication application);
	
}

