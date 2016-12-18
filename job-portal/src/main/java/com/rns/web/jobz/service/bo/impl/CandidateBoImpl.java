package com.rns.web.jobz.service.bo.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
import com.rns.web.jobz.service.dao.domain.Unsubscribers;
import com.rns.web.jobz.service.dao.impl.CandidateDaoImpl;
import com.rns.web.jobz.service.util.BTDConverter;
import com.rns.web.jobz.service.util.CommonUtils;
import com.rns.web.jobz.service.util.DTBConverter;
import com.rns.web.jobz.service.util.JobzConstants;
import com.rns.web.jobz.service.util.JobzMailUtil;
import com.rns.web.jobz.service.util.JobzUtils;
import com.rns.web.jobz.service.util.LoggingUtil;

public class CandidateBoImpl implements CandidateBo, JobzConstants {

	private SessionFactory sessionFactory;
	
	private ThreadPoolTaskExecutor executor;
	

	public ThreadPoolTaskExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}


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
				String activationCode = JobzUtils.generateActivationCode(candidate);
				candidate.setActivationCode(activationCode);
				candidates.setActivationCode(activationCode);
				candidates.setStatus(INACTIVE);
				candidateDaoImpl.addCandidate(candidates, session);
				JobzMailUtil mailer = new JobzMailUtil(MAIL_TYPE_ACTIVATION);
				mailer.setCandidate(candidate);
				executor.execute(mailer);
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
			if(application.getPoc() != null && candidateDaoImpl.getUnsubscriber(application.getPoc().getEmail(), session) != null) {
				CommonUtils.closeSession(session);
				return ERROR_UNSUBCRIBED_SERVICE;
			}
			JobPost post = BTDConverter.getJobPost(application);
			Candidates candidateByEmail = candidateDaoImpl.getCandidateByEmail(application.getPostedBy().getEmail(), session);
			if (post != null && candidateByEmail != null) {
				post.setPostedBy(candidateByEmail);
				post.setEducation(checkForAvailableEducation(session, BTDConverter.getEducations(application.getEducationRequired())));
				post.setSkills(checkForAvailableSkills(session, BTDConverter.getSkills(application.getSkillsRequired())));
				Transaction tx = session.beginTransaction();
				session.persist(post);
				tx.commit();
				JobApplication jobApplication = DTBConverter.getJobApplication(post);
				if(BTDConverter.isPoc(application)) {
					JobzMailUtil pocAlert = new JobzMailUtil(MAIL_TYPE_NEW_JOB_POC);
					application.setPostedBy(application.getPoc());
					application.setId(jobApplication.getId());
					pocAlert.setJobApplication(application);
					executor.execute(pocAlert);
				}
				JobzMailUtil mailer = new JobzMailUtil(MAIL_TYPE_NEW_JOB);
				mailer.setCandidates(DTBConverter.getMatchingCandidates(session, application.getPostedBy(), jobApplication));
				mailer.setJobApplication(jobApplication);
				executor.execute(mailer);
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
				
				JobzMailUtil jobzMailUtil = new JobzMailUtil();
				JobApplication jobApplication = DTBConverter.getJobApplication(candidateApplication);
				if(BTDConverter.isPoc(jobApplication)) {
					jobApplication.setPostedBy(jobApplication.getPoc());
				}
				BigDecimal compatibility = JobzUtils.calculateCompatibility(jobApplication.getCurrentCandidate(), jobApplication);
				jobApplication.setCompatibility(compatibility);
				jobzMailUtil.setJobApplication(jobApplication);
				jobzMailUtil.setCandidate(jobApplication.getCurrentCandidate());
				if(StringUtils.equals(YES,candidateApplication.getInterestShownByPoster()) && StringUtils.equals(YES,candidateApplication.getInterestShownBySeeker())) {
					if(StringUtils.equals(YES, application.getInterestShownByPoster())) {
						jobzMailUtil.setType(MAIL_TYPE_GOT_POSTER_CONTACT);
						executor.execute(jobzMailUtil);
					} else {
						jobzMailUtil.setType(MAIL_TYPE_GOT_SEEKER_CONTACT);
						executor.execute(jobzMailUtil);
					}
				} else if (StringUtils.equals(YES,candidateApplication.getInterestShownByPoster())) {
					jobzMailUtil.setType(MAIL_TYPE_POSTER_APPLY);
					executor.execute(jobzMailUtil);
				} else if(StringUtils.equals(NO,candidateApplication.getInterestShownByPoster())) { 
					jobzMailUtil.setType(MAIL_TYPE_POSTER_REJECTED);
					executor.execute(jobzMailUtil);
				}
				else {
					if(application.isAttachCv() && StringUtils.isNotBlank(candidateByEmail.getFilePath())) {
						 jobzMailUtil.getCandidate().setResume(new File(candidateByEmail.getFilePath()));
						 jobzMailUtil.getCandidate().setFilePath(candidateByEmail.getName() + "_Resume"  +"." + CommonUtils.getFileName(candidateByEmail.getFilePath()));
					}
					jobzMailUtil.setType(MAIL_TYPE_SEEKER_APPLY);
					if(BTDConverter.isPoc(jobApplication)) {
						jobApplication.setPostedBy(jobApplication.getPoc());
						jobApplication.setAttachCv(application.isAttachCv());
					}
					if(jobApplication.getPostedBy() != null && candidateDaoImpl.getUnsubscriber(jobApplication.getPostedBy().getEmail(), session) == null) {
						executor.execute(jobzMailUtil);
					}
				}
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
				currentCandidate.setActivationCode("");
				if(candidates.getLastLogin() == null || !DateUtils.isSameDay(new Date(), candidates.getLastLogin())) {
					Transaction tx = session.beginTransaction();
					candidates.setLastLogin(new Date());
					if(candidates.getNoOfVisits() == null) {
						candidates.setNoOfVisits(1);
					} else {
						candidates.setNoOfVisits(candidates.getNoOfVisits() + 1);
					}
					tx.commit();
				}
				
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



	@Override
	public Candidate activateCandidate(Candidate candidate) {
		if (candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return null;
		}
		Session session = null;
		Candidate curCandidate = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates registeredCandidate = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if(registeredCandidate == null) {
				CommonUtils.closeSession(session);
				return curCandidate;
			}
			else if(!StringUtils.equals(candidate.getActivationCode(), registeredCandidate.getActivationCode())) {
				CommonUtils.closeSession(session);
				return curCandidate;
			}
			Transaction tx = session.beginTransaction();
			registeredCandidate.setStatus(ACTIVE);
			JobzMailUtil mailer = new JobzMailUtil(MAIL_TYPE_REGISTRATION);
			curCandidate = DTBConverter.getCandidateBasic(registeredCandidate);
			mailer.setCandidate(curCandidate);
			executor.execute(mailer);
			tx.commit();
		} catch (Exception e) {
			
		} finally {
			CommonUtils.closeSession(session);
		}
		return curCandidate;
	}


	@Override
	public String forgotPassword(Candidate candidate) {
		if(candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		Session session = null;
		String result = ERROR_EMAIL_DOES_NOT_EXIST;
		try {
			session = this.sessionFactory.openSession();
			Candidates candidates = new CandidateDaoImpl().getCandidateByEmail(candidate.getEmail(), session);
			if(candidates != null) {
				JobzMailUtil mailUtil = new JobzMailUtil(MAIL_TYPE_FORGOT_PWD);
				mailUtil.setCandidate(DTBConverter.getCandidateBasic(candidates));
				executor.execute(mailUtil);
				result = RESPONSE_OK;
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	

	@Override
	public String uploadResume(Candidate candidate) {
		if(candidate == null || StringUtils.isEmpty(candidate.getEmail()) || candidate.getFile() == null) {
			return ERROR_INVALID_FILE;
		}
		Session session = null;
		String result = ERROR_IN_PROCESSING;
		BufferedOutputStream stream = null;
		try {
			session = this.sessionFactory.openSession();
			Candidates candidates = new CandidateDaoImpl().getCandidateByEmail(candidate.getEmail(), session);
			if(candidates != null) {
				String filePath = CommonUtils.getFilePath(candidates);
				File dir = new File(filePath);
				dir.mkdirs();
				String actualFilePath = filePath + "/" + candidate.getFilePath();
				File document = new File(actualFilePath);
				stream = new BufferedOutputStream(new FileOutputStream(document));
				stream.write(IOUtils.toByteArray(candidate.getFile()));
				Transaction tx = session.beginTransaction();
				candidates.setFilePath(actualFilePath);
				tx.commit();
				result = RESPONSE_OK;
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}
	
	@Override
	public Candidate downloadResume(Candidate candidate) {
		if(candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return null;
		}
		Session session = null;
		Candidate currentCandidate = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates candidates = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if(candidates != null && StringUtils.isNotEmpty(candidates.getFilePath())) {
				File file = new File(candidates.getFilePath());
				currentCandidate = DTBConverter.getCandidateBasic(candidates);
				currentCandidate.setResume(file);
				currentCandidate.setFilePath(currentCandidate.getName() + "." + CommonUtils.getFileName(candidates.getFilePath()));
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return currentCandidate;
	}

	@Override
	public String resendActivation(Candidate candidate) {
		if (candidate == null || StringUtils.isEmpty(candidate.getEmail())) {
			return null;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			Candidates registeredCandidate = candidateDaoImpl.getCandidateByEmail(candidate.getEmail(), session);
			if(registeredCandidate != null) {
				JobzMailUtil mailUtil = new JobzMailUtil(MAIL_TYPE_ACTIVATION);
				mailUtil.setCandidate(DTBConverter.getCandidateBasic(registeredCandidate));
				executor.execute(mailUtil);
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

	@Override
	public String unsubscribe(Candidate candidate) {
		if(candidate == null || StringUtils.isBlank(candidate.getEmail()) || candidate.getId() == null) {
			return ERROR_INCOMPLETE_DETAILS;
		}
		String result = RESPONSE_OK;
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			JobPost post = new CandidateDaoImpl().getPocJob(candidate.getEmail(), candidate.getId(), session);
			if(post != null) {
				Transaction tx = session.beginTransaction();
				Unsubscribers unsubscribers = new Unsubscribers();
				unsubscribers.setEmail(candidate.getEmail());
				unsubscribers.setDate(new Date());
				session.persist(unsubscribers);
				tx.commit();
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}
	
	@Override
	public JobApplication getJob(JobApplication applyJobRequested) {
		if(applyJobRequested == null || applyJobRequested.getId() == null) {
			return null;
		}
		JobApplication job = new JobApplication();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			CandidateDaoImpl candidateDaoImpl = new CandidateDaoImpl();
			JobPost post = candidateDaoImpl.getJobApplication(applyJobRequested.getId(), session);
			if(post != null) {
				job = DTBConverter.getJobApplication(post);
				if(applyJobRequested.getCurrentCandidate() != null && StringUtils.isNotEmpty(applyJobRequested.getCurrentCandidate().getEmail())) {
					Candidate currCandidate = DTBConverter.getCandidateBasic(candidateDaoImpl.getCandidateByEmail(applyJobRequested.getCurrentCandidate().getEmail(), session));
					if(currCandidate != null) {
						job.setCompatibility(JobzUtils.calculateCompatibility(currCandidate, job));
					}
				}
			}
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return job;
	}

}
