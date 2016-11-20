package com.rns.web.jobz.service.bo.domain;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Candidate {
	
	private Integer id;
	private String name;
	private String designation;
	private String company;
	private BigDecimal experience;
	private String description;
	private String email;
	private String phone;
	private String password;
	private String type;
	private List<JobSkill> jobSkills;
	private List<Qualification> educations;
	private List<JobApplication> postedJobs;
	private List<JobApplication> appliedJobs;
	private List<JobApplication> availableJobs;
	private List<JobApplication> acceptedJobs;
	private List<JobApplication> jobRequests;
	private List<Candidate> profileInterests;
	private List<Candidate> acceptedProfiles;
	private List<Candidate> interestsReceived;
	private BigDecimal compatibility;
	private Date appliedDate;
	private String seekerInterested;
	private String posterInterested;
	private JobSector sector;
	private String status;
	private String activationCode;
	private JobApplication application;
	private String filePath;
	private InputStream file; 
	private File resume;
	private Date lastLogin;
	private Integer noOfVisits;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public BigDecimal getExperience() {
		return experience;
	}
	public void setExperience(BigDecimal experience) {
		this.experience = experience;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<JobSkill> getJobSkills() {
		return jobSkills;
	}
	public void setJobSkills(List<JobSkill> jobSkills) {
		this.jobSkills = jobSkills;
	}
	public List<Qualification> getEducations() {
		return educations;
	}
	public void setEducations(List<Qualification> educations) {
		this.educations = educations;
	}
	public List<JobApplication> getPostedJobs() {
		return postedJobs;
	}
	public void setPostedJobs(List<JobApplication> postedJobs) {
		this.postedJobs = postedJobs;
	}
	public List<JobApplication> getAppliedJobs() {
		return appliedJobs;
	}
	public void setAppliedJobs(List<JobApplication> appliedJobs) {
		this.appliedJobs = appliedJobs;
	}
	public List<JobApplication> getAvailableJobs() {
		return availableJobs;
	}
	public void setAvailableJobs(List<JobApplication> availableJobs) {
		this.availableJobs = availableJobs;
	}
	public BigDecimal getCompatibility() {
		return compatibility;
	}
	public void setCompatibility(BigDecimal compatibility) {
		this.compatibility = compatibility;
	}
	public Date getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}
	public List<Candidate> getProfileInterests() {
		return profileInterests;
	}
	public void setProfileInterests(List<Candidate> profileInterests) {
		this.profileInterests = profileInterests;
	}
	public List<Candidate> getAcceptedProfiles() {
		return acceptedProfiles;
	}
	public void setAcceptedProfiles(List<Candidate> acceptedProfiles) {
		this.acceptedProfiles = acceptedProfiles;
	}
	public List<JobApplication> getJobRequests() {
		return jobRequests;
	}
	public void setJobRequests(List<JobApplication> jobRequests) {
		this.jobRequests = jobRequests;
	}
	public List<JobApplication> getAcceptedJobs() {
		return acceptedJobs;
	}
	public void setAcceptedJobs(List<JobApplication> acceptedJobs) {
		this.acceptedJobs = acceptedJobs;
	}
	public String getSeekerInterested() {
		return seekerInterested;
	}
	public void setSeekerInterested(String seekerInterested) {
		this.seekerInterested = seekerInterested;
	}
	public String getPosterInterested() {
		return posterInterested;
	}
	public void setPosterInterested(String posterInterested) {
		this.posterInterested = posterInterested;
	}
	public JobSector getSector() {
		return sector;
	}
	public void setSector(JobSector sector) {
		this.sector = sector;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getActivationCode() {
		return activationCode;
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	public JobApplication getApplication() {
		return application;
	}
	public void setApplication(JobApplication application) {
		this.application = application;
	}
	public List<Candidate> getInterestsReceived() {
		return interestsReceived;
	}
	public void setInterestsReceived(List<Candidate> interestsReceived) {
		this.interestsReceived = interestsReceived;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public InputStream getFile() {
		return file;
	}
	public void setFile(InputStream file) {
		this.file = file;
	}
	public File getResume() {
		return resume;
	}
	public void setResume(File resume) {
		this.resume = resume;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Integer getNoOfVisits() {
		return noOfVisits;
	}
	public void setNoOfVisits(Integer noOfVisits) {
		this.noOfVisits = noOfVisits;
	}
	
}
