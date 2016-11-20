package com.rns.web.jobz.service.bo.domain;

import java.util.List;

public class AdminResponse {
	
	private List<JobApplication> postedJobs;
	private List<Candidate> candidates;
	private String result;

	public List<JobApplication> getPostedJobs() {
		return postedJobs;
	}

	public void setPostedJobs(List<JobApplication> postedJobs) {
		this.postedJobs = postedJobs;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
