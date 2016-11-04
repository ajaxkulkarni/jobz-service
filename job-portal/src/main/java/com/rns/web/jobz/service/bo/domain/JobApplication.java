package com.rns.web.jobz.service.bo.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class JobApplication {

	private Integer id;
	private Date postedDate;
	private Date appliedDate;
	private Date expiryDate;
	private String location;
	private String type;
	private BigDecimal minExperience;
	private BigDecimal maxExperience;
	private Integer salary;
	private String description;
	private Integer maxApplicants;
	private String jobTitle;
	private String companyName;
	private Candidate postedBy;
	private List<JobSkill> skillsRequired;
	private List<Qualification> educationRequired;
	private List<Candidate> applications;
	private Integer noOfApplications;
	private List<Candidate> matchingCandidates;
	private Integer noOfMatches;
	private List<Candidate> interestCandidates;
	private Integer noOfInterests;
	private Candidate currentCandidate;
	private String interestShownByPoster;
	private String interestShownBySeeker;
	private BigDecimal compatibility;
	private JobSector sector;
	private String expiryDateString;
	private String postedDateString;
	private boolean isExpired;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getMaxApplicants() {
		return maxApplicants;
	}
	public void setMaxApplicants(Integer maxApplicants) {
		this.maxApplicants = maxApplicants;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Candidate getPostedBy() {
		return postedBy;
	}
	public void setPostedBy(Candidate postedBy) {
		this.postedBy = postedBy;
	}
	public List<JobSkill> getSkillsRequired() {
		return skillsRequired;
	}
	public void setSkillsRequired(List<JobSkill> skillsRequired) {
		this.skillsRequired = skillsRequired;
	}
	public List<Qualification> getEducationRequired() {
		return educationRequired;
	}
	public void setEducationRequired(List<Qualification> educationRequired) {
		this.educationRequired = educationRequired;
	}
	public List<Candidate> getApplications() {
		return applications;
	}
	public void setApplications(List<Candidate> applications) {
		this.applications = applications;
	}
	public Candidate getCurrentCandidate() {
		return currentCandidate;
	}
	public void setCurrentCandidate(Candidate currentCandidate) {
		this.currentCandidate = currentCandidate;
	}
	public String getInterestShownByPoster() {
		return interestShownByPoster;
	}
	public void setInterestShownByPoster(String interestShownByPoster) {
		this.interestShownByPoster = interestShownByPoster;
	}
	public Date getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}
	public BigDecimal getCompatibility() {
		return compatibility;
	}
	public void setCompatibility(BigDecimal compatibility) {
		this.compatibility = compatibility;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public List<Candidate> getMatchingCandidates() {
		return matchingCandidates;
	}
	public void setMatchingCandidates(List<Candidate> matchingCandidates) {
		this.matchingCandidates = matchingCandidates;
	}
	public List<Candidate> getInterestCandidates() {
		return interestCandidates;
	}
	public void setInterestCandidates(List<Candidate> interestCandidates) {
		this.interestCandidates = interestCandidates;
	}
	public String getInterestShownBySeeker() {
		return interestShownBySeeker;
	}
	public void setInterestShownBySeeker(String interestShownBySeeker) {
		this.interestShownBySeeker = interestShownBySeeker;
	}
	public JobSector getSector() {
		return sector;
	}
	public void setSector(JobSector sector) {
		this.sector = sector;
	}
	public BigDecimal getMinExperience() {
		return minExperience;
	}
	public void setMinExperience(BigDecimal minExperience) {
		this.minExperience = minExperience;
	}
	public BigDecimal getMaxExperience() {
		return maxExperience;
	}
	public void setMaxExperience(BigDecimal maxExperience) {
		this.maxExperience = maxExperience;
	}
	public String getExpiryDateString() {
		return expiryDateString;
	}
	public void setExpiryDateString(String expiryDateString) {
		this.expiryDateString = expiryDateString;
	}
	public Integer getNoOfApplications() {
		return noOfApplications;
	}
	public void setNoOfApplications(Integer noOfApplications) {
		this.noOfApplications = noOfApplications;
	}
	public Integer getNoOfMatches() {
		return noOfMatches;
	}
	public void setNoOfMatches(Integer noOfMatches) {
		this.noOfMatches = noOfMatches;
	}
	public Integer getNoOfInterests() {
		return noOfInterests;
	}
	public void setNoOfInterests(Integer noOfInterests) {
		this.noOfInterests = noOfInterests;
	}
	public String getPostedDateString() {
		return postedDateString;
	}
	public void setPostedDateString(String postedDateString) {
		this.postedDateString = postedDateString;
	}
	public boolean isExpired() {
		return isExpired;
	}
	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}
	
	
}