package com.rns.web.jobz.service.bo.domain;

import java.util.List;

public class JobServiceResponse {
	
	private Integer status;
	private String responseText;
	private Candidate candidateProfile;
	private List<JobSkill> matchingJobSkills;
	private List<Qualification> matchingEducations;
	private AdminResponse adminResponse;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public Candidate getCandidateProfile() {
		return candidateProfile;
	}
	public void setCandidateProfile(Candidate candidateProfile) {
		this.candidateProfile = candidateProfile;
	}
	public List<JobSkill> getMatchingJobSkills() {
		return matchingJobSkills;
	}
	public void setMatchingJobSkills(List<JobSkill> matchingJobSkills) {
		this.matchingJobSkills = matchingJobSkills;
	}
	public List<Qualification> getMatchingEducations() {
		return matchingEducations;
	}
	public void setMatchingEducations(List<Qualification> matchingEducations) {
		this.matchingEducations = matchingEducations;
	}
	public AdminResponse getAdminResponse() {
		return adminResponse;
	}
	public void setAdminResponse(AdminResponse adminResponse) {
		this.adminResponse = adminResponse;
	}

}
