package com.rns.web.jobz.service.bo.domain;

public class JobServiceRequest {

	private Candidate requestedBy;
	private String jobSkillRequested;
	private String educationRequested;
	private JobApplication postJobRequested;
	private JobApplication applyJobRequested;
	private AdminMail mailer;
	
	public Candidate getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(Candidate requestedBy) {
		this.requestedBy = requestedBy;
	}
	public String getJobSkillRequested() {
		return jobSkillRequested;
	}
	public void setJobSkillRequested(String jobSkillRequested) {
		this.jobSkillRequested = jobSkillRequested;
	}
	public String getEducationRequested() {
		return educationRequested;
	}
	public void setEducationRequested(String educationRequested) {
		this.educationRequested = educationRequested;
	}
	public JobApplication getPostJobRequested() {
		return postJobRequested;
	}
	public void setPostJobRequested(JobApplication postJobRequested) {
		this.postJobRequested = postJobRequested;
	}
	public JobApplication getApplyJobRequested() {
		return applyJobRequested;
	}
	public void setApplyJobRequested(JobApplication applyJobRequested) {
		this.applyJobRequested = applyJobRequested;
	}
	public AdminMail getMailer() {
		return mailer;
	}
	public void setMailer(AdminMail mailer) {
		this.mailer = mailer;
	}
	
}
