package com.rns.web.jobz.service.bo.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.rns.web.jobz.service.bo.api.CandidateBo;
import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;
import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.Candidates;
import com.rns.web.jobz.service.dao.domain.Education;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.domain.Skills;
import com.rns.web.jobz.service.dao.impl.CandidateDaoImpl;
import com.rns.web.jobz.service.util.BTDConverter;
import com.rns.web.jobz.service.util.CommonUtils;
import com.rns.web.jobz.service.util.DTBConverter;
import com.rns.web.jobz.service.util.JobzConstants;
import com.rns.web.jobz.service.util.LoggingUtil;

public class CandidateBoImpl implements CandidateBo, JobzConstants {

	private SessionFactory sessionFactory;
	

	@Override
	public String registerCandidate(Candidate candidate) {
		if (candidate == null || StringUtils.isEmpty(candidate.getName()) || StringUtils.isEmpty(candidate.getEmail())) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates registeredCandidate = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if (registeredCandidate == null) {
				Candidates candidates = BTDConverter.getCandidates(candidate);
				Transaction tx = session.beginTransaction();
				candidates.setEducation(checkForAvailableEducation(session, BTDConverter.getEducations(candidate.getEducations())));
				candidates.setSkills(checkForAvailableSkills(session, BTDConverter.getSkills(candidate.getJobSkills())));
				candidateDaoImpl.addCandidate(candidates, session);
				tx.commit();
			} else {
				result = ERROR_EMAIL_EXISTS;
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		
		return result;
	}

	

	private Set<Education> checkForAvailableEducation(Session session, Set<Education> education2) {
		if (CollectionUtils.isEmpty(education2)) {
			return education2;
		}
		Set<Education> filteredEducations = new HashSet<Education>();
		CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
		for (Education education : education2) {
			Education educationByName = candidateDaoImpl.getEducationByName(education.getName(), session);
			if (educationByName != null) {
				filteredEducations.add(educationByName);
			} else {
				filteredEducations.add(education);
			}
		}

		return filteredEducations;
	}

	private Set<Skills> checkForAvailableSkills(Session session, Set<Skills> skills) {
		if (CollectionUtils.isEmpty(skills)) {
			return skills;
		}
		Set<Skills> filteredSkills = new HashSet<Skills>();
		CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
		for (Skills skill : skills) {
			Skills skillByName = candidateDaoImpl.getSkillByName(skill.getName(), session);
			if (skillByName != null) {
				filteredSkills.add(skillByName);
			} else {
				filteredSkills.add(skill);
			}
		}

		return filteredSkills;
	}

	@Override
	public String updateCandidate(Candidate candidate) {
		if (candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates registeredCandidate = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if (registeredCandidate != null) {
				BTDConverter.setCandidates(candidate, registeredCandidate);
				registeredCandidate.setEducation(checkForAvailableEducation(session, BTDConverter.getEducations(candidate.getEducations())));
				registeredCandidate.setSkills(checkForAvailableSkills(session, BTDConverter.getSkills(candidate.getJobSkills())));
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		
		return result;
	}

	@Override
	public Candidate login(Candidate candidate) {
		if (candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return null;
		}
		Candidate loginCandidate = null;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates registeredCandidate = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if(registeredCandidate == null) {
				return null;
			}
			else if(!StringUtils.equals(candidate.getPassword(), registeredCandidate.getPassword())) {
				return null;
			}
			loginCandidate = DTBConverter.getCandidateBasic(registeredCandidate);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return loginCandidate;
	}

	@Override
	public String postNewJob(JobApplication application) {
		if (application == null || application.getPostedBy() == null || StringUtils.isEmpty(application.getPostedBy().getEmail())) {
			return ERROR_INCOMPLETE_JOB_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			JobPost post = BTDConverter.getJobPost(application);
			Candidates candidateByEmail = candidateDaoImpl.getCandidateByEmail(application.getPostedBy().getEmail(), session);
			if (post != null && candidateByEmail != null) {
				post.setPostedBy(candidateByEmail);
				post.setEducation(checkForAvailableEducation(session, BTDConverter.getEducations(application.getEducationRequired())));
				post.setSkills(checkForAvailableSkills(session, BTDConverter.getSkills(application.getSkillsRequired())));
				Transaction tx = session.beginTransaction();
				session.persist(post);
				tx.commit();
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	@Override
	public String applyForJob(JobApplication application) {
		if (application == null || application.getId() == null || application.getCurrentCandidate() == null) {
			return ERROR_INCOMPLETE_JOB_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session= this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			JobPost post = (JobPost) session.get(JobPost.class, application.getId());
			Candidates candidateByEmail = candidateDaoImpl.getCandidateByEmail(application.getCurrentCandidate().getEmail(), session);
			if (post != null) {
				CandidateApplication candidateApplication = candidateDaoImpl.getApplication(application.getId(), application.getCurrentCandidate().getEmail(), session);
				if(candidateApplication == null) {
					candidateApplication = new CandidateApplication();
					candidateApplication.setJobPost(post);
				}
				candidateApplication.setAppliedDate(new Date());
				if (candidateByEmail != null) {
					candidateApplication.setCandidates(candidateByEmail);
					if(StringUtils.isNotEmpty(application.getInterestShownBySeeker())) {
						candidateApplication.setInterestShownBySeeker(application.getInterestShownBySeeker());
					}
				}
				if(StringUtils.isNotEmpty(application.getInterestShownByPoster())) {
					candidateApplication.setInterestShownByPoster(application.getInterestShownByPoster());
				}
				
				Transaction tx = session.beginTransaction();
				session.persist(candidateApplication);
				tx.commit();
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	@Override
	public Candidate populateCandidate(Candidate candidate) {
		if(candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return null;
		}
		Candidate currentCandidate = new Candidate();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates candidates = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if(candidates != null) {
				currentCandidate = DTBConverter.getCandidate(candidates, session);
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return currentCandidate;
	}

	
	@Override
	public String updateJob(JobApplication application) {
		if (application == null || application.getId() == null) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			JobPost post = candidateDaoImpl.getJobApplication(application.getId(), session);
			if (post != null) {
				BTDConverter.setJobPost(application, post);
				post.setEducation(checkForAvailableEducation(session, BTDConverter.getEducations(application.getEducationRequired())));
				post.setSkills(checkForAvailableSkills(session, BTDConverter.getSkills(application.getSkillsRequired())));
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	
	@Override
	public String deleteJob(JobApplication application) {
		if (application == null || application.getId() == null) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			JobPost post = candidateDaoImpl.getJobApplication(application.getId(), session);
			if (post != null) {
				session.delete(post);
			}
			tx.commit();
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = e.getMessage();
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	

	@Override
	public List<JobSkill> getAvailableSkills(JobSkill skill) {
		if(skill == null) {
			return null;
		}
		Session session = null;
		List<JobSkill> jobSkills = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			List<Skills> skills = candidateDaoImpl.getSkillsByname(skill.getName(), session);
			jobSkills = DTBConverter.getJobSkills(new HashSet<Skills>(skills));
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return jobSkills;
	}
	
	@Override
	public List<Qualification> getAvailableQualifications(Qualification qualification) {
		if(qualification == null) {
			return null;
		}
		List<Qualification> qualifications = null;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			List<Education> educations = candidateDaoImpl.getEducationsByName(qualification.getName(), session);
			qualifications = DTBConverter.getQualifications(new HashSet<Education>(educations));
		} catch (Exception e) {
			
		} finally {
			CommonUtils.closeSession(session);
		}
		return qualifications;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
