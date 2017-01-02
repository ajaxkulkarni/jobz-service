package com.rns.web.jobz.service.dao.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "candidate_application")
public class CandidateApplication {

	private Integer id;
	private String interestShownByPoster;
	private String interestShownBySeeker;
	private Date appliedDate;
	private Candidates candidates;
	private JobPost jobPost;
	private String resumeDownloaded;
	private String resumeSent;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "interest_shown_by_poster")
	public String getInterestShownByPoster() {
		return interestShownByPoster;
	}
	public void setInterestShownByPoster(String interestShownByPoster) {
		this.interestShownByPoster = interestShownByPoster;
	}
	
	@Column(name = "interest_shown_by_seeker")
	public String getInterestShownBySeeker() {
		return interestShownBySeeker;
	}
	public void setInterestShownBySeeker(String interestShownBySeeker) {
		this.interestShownBySeeker = interestShownBySeeker;
	}
	
	@Column(name = "applied_date")
	public Date getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "candidate_id")
	public Candidates getCandidates() {
		return candidates;
	}
	public void setCandidates(Candidates candidates) {
		this.candidates = candidates;
	}
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "job_post_id")
	public JobPost getJobPost() {
		return jobPost;
	}
	public void setJobPost(JobPost jobPost) {
		this.jobPost = jobPost;
	}
	
	@Column(name = "resume_download")
	public String getResumeDownloaded() {
		return resumeDownloaded;
	}
	public void setResumeDownloaded(String resumeDownloaded) {
		this.resumeDownloaded = resumeDownloaded;
	}
	
	@Column(name = "resume_sent")
	public String getResumeSent() {
		return resumeSent;
	}
	public void setResumeSent(String resumeSent) {
		this.resumeSent = resumeSent;
	}
	
	
}
