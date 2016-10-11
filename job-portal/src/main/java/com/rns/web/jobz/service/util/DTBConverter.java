package com.rns.web.jobz.service.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.bo.domain.JobSector;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;
import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.Candidates;
import com.rns.web.jobz.service.dao.domain.Education;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.domain.Skills;
import com.rns.web.jobz.service.dao.impl.CandidateDaoImpl;

public class DTBConverter {

	public static Candidate getCandidate(Candidates candidates, Session session) {
		Candidate currentCandidate = getCandidateBasic(candidates);

		getPostedJobs(candidates, session, currentCandidate);

		getAppliedJobs(candidates, currentCandidate);

		getAvailableJobs(currentCandidate, session, candidates);

		return currentCandidate;
	}

	private static void getAppliedJobs(Candidates candidates, Candidate currentCandidate) {
		List<JobApplication> appliedJobs = new ArrayList<JobApplication>();
		List<JobApplication> acceptedJobs = new ArrayList<JobApplication>();
		List<JobApplication> jobRequests = new ArrayList<JobApplication>();
		if (CollectionUtils.isNotEmpty(candidates.getApplications())) {
			for (CandidateApplication application : candidates.getApplications()) {
				JobApplication jobApplication = getJobApplication(application);
				if(jobApplication == null) {
					continue;
				}
				BigDecimal compatibility = JobzUtils.calculateCompatibility(currentCandidate, jobApplication);
				jobApplication.setCompatibility(compatibility);
				if (StringUtils.equals(JobzConstants.YES, application.getInterestShownByPoster())) {
					if (StringUtils.equals(JobzConstants.YES, application.getInterestShownBySeeker())) {
						acceptedJobs.add(jobApplication);
					} else {
						jobRequests.add(jobApplication);
					}
				} else {
					appliedJobs.add(jobApplication);
				}
			}

		}

		currentCandidate.setJobRequests(JobzUtils.sortByCompatibility(jobRequests));
		currentCandidate.setAcceptedJobs(JobzUtils.sortByCompatibility(acceptedJobs));
		currentCandidate.setAppliedJobs(JobzUtils.sortByCompatibility(appliedJobs));
	}

	public static JobApplication getJobApplication(CandidateApplication application) {
		if (application == null || application.getJobPost() == null) {
			return null;
		}
		JobApplication jobApplication = getJobApplication(application.getJobPost());
		jobApplication.setCurrentCandidate(getCandidateBasic(application.getCandidates()));
		jobApplication.setInterestShownByPoster(application.getInterestShownByPoster());
		jobApplication.setPostedBy(getCandidateBasic(application.getJobPost().getPostedBy()));
		jobApplication.setAppliedDate(application.getAppliedDate());
		jobApplication.setInterestShownBySeeker(application.getInterestShownBySeeker());
		return jobApplication;
	}

	private static void getPostedJobs(Candidates candidates, Session session, Candidate currentCandidate) {
		List<JobApplication> postedJobs = new ArrayList<JobApplication>();
		List<Candidate> profileInterests = new ArrayList<Candidate>();
		List<Candidate> acceptedProfiles = new ArrayList<Candidate>();
		if (CollectionUtils.isNotEmpty(candidates.getPosts())) {
			for (JobPost post : candidates.getPosts()) {
				JobApplication application = getJobApplication(post);
				List<Candidate> applicants = new ArrayList<Candidate>();
				List<Candidate> interestCandidates = new ArrayList<Candidate>();
				for (CandidateApplication candi : post.getApplications()) {
					if (candi.getCandidates() != null) {
						Candidate candidate = getCandidateBasic(candi.getCandidates());
						BigDecimal compatibility = JobzUtils.calculateCompatibility(candidate, application);
						candidate.setCompatibility(compatibility);
						candidate.setAppliedDate(candi.getAppliedDate());
						candidate.setSeekerInterested(candi.getInterestShownBySeeker());
						candidate.setPosterInterested(candi.getInterestShownByPoster());
						if (StringUtils.equals(JobzConstants.YES, candi.getInterestShownByPoster())) {
							// candidate.setPosterInterested(JobzConstants.YES);
							interestCandidates.add(candidate);
							profileInterests.add(candidate);
							if (StringUtils.equals(JobzConstants.YES, candi.getInterestShownBySeeker())) {
								JobApplication shortListedFor = new JobApplication();
								shortListedFor.setJobTitle(application.getJobTitle());
								shortListedFor.setCompanyName(application.getCompanyName());
								candidate.setApplication(shortListedFor);
								acceptedProfiles.add(candidate);
							}
						} else {
							applicants.add(candidate);
						}

					}
				}
				application.setInterestCandidates(JobzUtils.sortByCompatibilityCandidates(interestCandidates));
				application.setApplications(JobzUtils.sortByCompatibilityCandidates(applicants));
				List<Candidate> matchingCandidates = getMatchingCandidates(session, currentCandidate, application);
				application.setMatchingCandidates(JobzUtils.sortByCompatibilityCandidates(matchingCandidates));
				postedJobs.add(application);
			}

		}

		currentCandidate.setAcceptedProfiles(JobzUtils.sortByCompatibilityCandidates(acceptedProfiles));
		currentCandidate.setProfileInterests(JobzUtils.sortByCompatibilityCandidates(profileInterests));
		currentCandidate.setPostedJobs(postedJobs);
		// JobzUtils.sortByCompatibility(postedJobs);
	}

	public static List<Candidate> getMatchingCandidates(Session session, Candidate currentCandidate, JobApplication application) {
		List<Candidates> allCandidates = new CandidateDaoImpl().getAllCandidates(session);
		List<Candidate> matchingCandidates = new ArrayList<Candidate>();
		for (Candidates availableCandidates : allCandidates) {
			if (StringUtils.equals(currentCandidate.getEmail(), availableCandidates.getEmail())) {
				continue;
			}
			if (candidatePresent(application, availableCandidates)) {
				continue;
			}
			Candidate candidate = getCandidateBasic(availableCandidates);
			BigDecimal compatibility = JobzUtils.calculateCompatibility(candidate, application);
			candidate.setCompatibility(compatibility);
			if (isCompatible(compatibility)) {
				matchingCandidates.add(candidate);
			}
		}
		return matchingCandidates;
	}

	private static boolean candidatePresent(JobApplication application, Candidates candidates) {
		if (candidates == null) {
			return false;
		}
		if (isCandidatePresent(candidates, application.getApplications())) {
			return true;
		}
		if (isCandidatePresent(candidates, application.getInterestCandidates())) {
			return true;
		}
		return false;
	}

	private static boolean isCandidatePresent(Candidates candidates, List<Candidate> applicants) {
		if (CollectionUtils.isNotEmpty(applicants)) {
			for (Candidate applier : applicants) {
				if (applier.getId().intValue() == candidates.getId().intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	private static void getAvailableJobs(Candidate currentCandidate, Session session, Candidates candidates) {
		List<JobPost> allJobs = new CandidateDaoImpl().getAllAvailableJobs(candidates.getId(), session);
		List<JobApplication> allApplications = new ArrayList<JobApplication>();
		if (CollectionUtils.isNotEmpty(allJobs)) {
			for (JobPost post : allJobs) {
				if (jobPresent(post, currentCandidate)) {
					continue;
				}
				JobApplication application = DTBConverter.getJobApplication(post);
				if(application == null) {
					continue;
				}
				application.setPostedBy(DTBConverter.getCandidateBasic(post.getPostedBy()));
				BigDecimal compatibility = JobzUtils.calculateCompatibility(currentCandidate, application);
				application.setCompatibility(compatibility);
				if (isCompatible(compatibility)) {
					allApplications.add(application);
				}

			}
			JobzUtils.sortByCompatibility(allApplications);
		}
		currentCandidate.setAvailableJobs(allApplications);
	}

	private static boolean isCompatible(BigDecimal compatibility) {
		return BigDecimal.ZERO.compareTo(compatibility) <= 0;
	}

	private static boolean jobPresent(JobPost post, Candidate candidate) {
		if (candidate == null) {
			return false;
		}
		if (isJobPresent(post, candidate.getAppliedJobs())) {
			return true;
		}
		if (isJobPresent(post, candidate.getAcceptedJobs())) {
			return true;
		}
		if (isJobPresent(post, candidate.getJobRequests())) {
			return true;
		}
		return false;
	}

	private static boolean isJobPresent(JobPost post, List<JobApplication> jobs) {
		if (CollectionUtils.isNotEmpty(jobs)) {
			for (JobApplication app : jobs) {
				if (app.getId().intValue() == post.getId().intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	public static Candidate getCandidateBasic(Candidates candidates) {
		Candidate currentCandidate = new Candidate();
		currentCandidate.setId(candidates.getId());
		currentCandidate.setCompany(candidates.getCompany());
		currentCandidate.setDescription(candidates.getDescription());
		currentCandidate.setDesignation(candidates.getDesignation());
		currentCandidate.setEmail(candidates.getEmail());
		currentCandidate.setExperience(candidates.getExperience());
		currentCandidate.setId(candidates.getId());
		currentCandidate.setName(candidates.getName());
		currentCandidate.setPassword(candidates.getPassword());
		currentCandidate.setPhone(candidates.getPhone());
		currentCandidate.setJobSkills(getJobSkills(candidates.getSkills()));
		currentCandidate.setEducations(getQualifications(candidates.getEducation()));
		currentCandidate.setType(candidates.getType());
		currentCandidate.setSector(getSector(candidates.getSector()));
		currentCandidate.setStatus(candidates.getStatus());
		return currentCandidate;
	}

	public static JobApplication getJobApplication(JobPost post) {
		JobApplication application = new JobApplication();
		application.setId(post.getId());
		application.setCompanyName(post.getCompanyName());
		application.setDescription(post.getDescription());
		application.setEducationRequired(getQualifications(post.getEducation()));
		application.setMinExperience(post.getMinExperience());
		application.setMaxExperience(post.getMaxExperience());
		application.setExpiryDate(post.getExpiryDate());
		application.setLocation(post.getLocation());
		application.setMaxApplicants(post.getMaxApplicants());
		application.setPostedDate(post.getPostedDate());
		application.setSalary(post.getSalary());
		application.setSkillsRequired(getJobSkills(post.getSkills()));
		application.setJobTitle(post.getJobTitle());
		application.setType(post.getType());
		application.setSector(getSector(post.getSector()));
		application.setExpiryDateString(CommonUtils.convertDate(application.getExpiryDate()));
		application.setPostedDateString(CommonUtils.convertDate(application.getPostedDate()));
		return application;
	}


	private static JobSector getSector(Integer sector2) {
		JobSector sector = new JobSector();
		sector.setId(sector2);
		sector.setName(JobzConstants.SECTORS.get(sector2));
		return sector;
	}

	public static List<Qualification> getQualifications(Set<Education> edus) {
		List<Qualification> educations = new ArrayList<Qualification>();
		if (CollectionUtils.isNotEmpty(edus)) {
			for (Education education : edus) {
				Qualification edu = new Qualification();
				edu.setId(education.getId());
				edu.setName(education.getName());
				educations.add(edu);
			}
		}
		return educations;
	}

	public static List<JobSkill> getJobSkills(Set<Skills> skillsList) {
		List<JobSkill> jobSkills = new ArrayList<JobSkill>();
		if (CollectionUtils.isNotEmpty(skillsList)) {
			for (Skills skills : skillsList) {
				JobSkill skill = new JobSkill();
				skill.setId(skills.getId());
				skill.setName(skills.getName());
				jobSkills.add(skill);
			}
		}
		return jobSkills;
	}
	
	public static List<JobApplication> getJobApplications(List<JobPost> jobPosts) {
		List<JobApplication> postedJobs;
		postedJobs = new ArrayList<JobApplication>();
		for(JobPost post: jobPosts) {
			JobApplication app = getJobApplication(post);
			app.setPostedBy(getCandidateBasic(post.getPostedBy()));
			if(CollectionUtils.isNotEmpty(post.getApplications())) {
				app.setNoOfApplications(post.getApplications().size());
			}
			/*if(CollectionUtils.isNotEmpty(post.get)) {
				app.setNoOfMatches(post.get);
			}*/
			if(CollectionUtils.isNotEmpty(post.getApplications())) {
				app.setNoOfApplications(post.getApplications().size());
			}
			if(app != null) {
				postedJobs.add(app);
			}
		}
		return postedJobs;
	}

	public static List<JobApplication> getJobApplicants(List<CandidateApplication> applications) {
		List<JobApplication> jobApplications = new ArrayList<JobApplication>();
		for(CandidateApplication post: applications) {
			JobApplication application = getJobApplication(post);
			if(application != null) {
				jobApplications.add(application);
			}
		}
		return jobApplications;
	}
	
}
