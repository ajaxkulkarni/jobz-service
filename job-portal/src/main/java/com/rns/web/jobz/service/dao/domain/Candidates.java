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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "candidates")
public class Candidates {

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
	private Set<Skills> skills = new HashSet<Skills>(0);
	private Set<Education> education = new HashSet<Education>(0);
	private Set<JobPost> posts = new HashSet<JobPost>();
	private Set<CandidateApplication> applications = new HashSet<CandidateApplication>();
	private Integer sector;
	private String status;
	private String activationCode;
	private String filePath;
	private Date lastLogin;
	private Integer noOfVisits;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "designation")
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@Column(name = "company")
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Column(name = "experience")
	public BigDecimal getExperience() {
		return experience;
	}
	
	public void setExperience(BigDecimal experience) {
		this.experience = experience;
	}
	
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSkills(Set<Skills> skills) {
		this.skills = skills;
	}
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name="candidate_skills", 
				joinColumns={@JoinColumn(name="candidate_id")}, 
				inverseJoinColumns={@JoinColumn(name="skill_id")})
	public Set<Skills> getSkills() {
		return skills;
	}

	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(name="candidate_education", 
				joinColumns={@JoinColumn(name="candidate_id")}, 
				inverseJoinColumns={@JoinColumn(name="education_id")})
	public Set<Education> getEducation() {
		return education;
	}

	public void setEducation(Set<Education> education) {
		this.education = education;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(mappedBy = "postedBy")
	@OrderBy("id DESC")
	public Set<JobPost> getPosts() {
		return posts;
	}

	public void setPosts(Set<JobPost> posts) {
		this.posts = posts;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(mappedBy = "candidates")
	@OrderBy("id DESC")
	public Set<CandidateApplication> getApplications() {
		return applications;
	}

	public void setApplications(Set<CandidateApplication> applications) {
		this.applications = applications;
	}

	public Integer getSector() {
		return sector;
	}

	@Column(name = "sector")
	public void setSector(Integer sector) {
		this.sector = sector;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "activation_code")
	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Column(name = "file_path")
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(name = "last_login")
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Column(name = "no_of_visits")
	public Integer getNoOfVisits() {
		return noOfVisits;
	}

	public void setNoOfVisits(Integer noOfVisits) {
		this.noOfVisits = noOfVisits;
	}
}
