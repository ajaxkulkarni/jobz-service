package com.rns.web.jobz.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "job_post")
public class JobPost {

	private Integer id;
	private Date postedDate;
	private Date expiryDate;
	private String location;
	private String type;
	private BigDecimal minExperience;
	private BigDecimal maxExperience;
	private Integer salary;
	private String description;
	private Integer maxApplicants;
	private String companyName;
	private String jobTitle;
	private Candidates postedBy;
	private Set<Skills> skills = new HashSet<Skills>(0);
	private Set<Education> education = new HashSet<Education>(0);
	private Set<CandidateApplication> applications = new HashSet<CandidateApplication>();
	private Integer sector;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "posted_date")
	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	
	@Column(name = "expiry_date")
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Column(name = "location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "min_experience")
	public BigDecimal getMinExperience() {
		return minExperience;
	}
	
	public void setMinExperience(BigDecimal minExperience) {
		this.minExperience = minExperience;
	}
	
	@Column(name = "max_experience")
	public BigDecimal getMaxExperience() {
		return maxExperience;
	}
	
	public void setMaxExperience(BigDecimal maxExperience) {
		this.maxExperience = maxExperience;
	}

	@Column(name = "salary")
	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "max_applicants")
	public Integer getMaxApplicants() {
		return maxApplicants;
	}

	public void setMaxApplicants(Integer maxApplicants) {
		this.maxApplicants = maxApplicants;
	}

	@Column(name = "company_name")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "posted_by")
	public Candidates getPostedBy() {
		return postedBy;
	}
	
	
	public void setPostedBy(Candidates postedBy) {
		this.postedBy = postedBy;
	}
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name="job_post_skills", 
				joinColumns={@JoinColumn(name="job_post_id")}, 
				inverseJoinColumns={@JoinColumn(name="skill_id")})
	public Set<Skills> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skills> skills) {
		this.skills = skills;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name="job_post_education", 
				joinColumns={@JoinColumn(name="job_post_id")}, 
				inverseJoinColumns={@JoinColumn(name="education_id")})
	public Set<Education> getEducation() {
		return education;
	}

	public void setEducation(Set<Education> education) {
		this.education = education;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(mappedBy = "jobPost")
	public Set<CandidateApplication> getApplications() {
		return applications;
	}

	public void setApplications(Set<CandidateApplication> applications) {
		this.applications = applications;
	}

	@Column(name = "job_title")
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	@Column(name = "sector")
	public Integer getSector() {
		return sector;
	}

	public void setSector(Integer sector) {
		this.sector = sector;
	}
	
}
