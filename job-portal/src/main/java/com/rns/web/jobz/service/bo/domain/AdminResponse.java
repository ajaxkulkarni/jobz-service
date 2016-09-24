package com.rns.web.jobz.service.bo.domain;

import java.util.List;

public class AdminResponse {
	
	private List<JobApplication> postedJobs;

	public List<JobApplication> getPostedJobs() {
		return postedJobs;
	}

	public void setPostedJobs(List<JobApplication> postedJobs) {
		this.postedJobs = postedJobs;
	}

}
