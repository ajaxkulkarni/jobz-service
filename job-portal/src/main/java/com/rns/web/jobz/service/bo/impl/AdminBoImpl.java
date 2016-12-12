package com.rns.web.jobz.service.bo.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rns.web.jobz.service.bo.api.AdminBo;
import com.rns.web.jobz.service.bo.domain.AdminMail;
import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.Candidates;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.impl.AdminDaoImpl;
import com.rns.web.jobz.service.util.CommonUtils;
import com.rns.web.jobz.service.util.DTBConverter;
import com.rns.web.jobz.service.util.JobzConstants;
import com.rns.web.jobz.service.util.JobzMailUtil;
import com.rns.web.jobz.service.util.LoggingUtil;

public class AdminBoImpl implements AdminBo {
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private ThreadPoolTaskExecutor executor;
	
	public ThreadPoolTaskExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}
	
	@Override
	public List<JobApplication> getAllPostedJobs() {
		List<JobApplication> postedJobs = null; 
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			List<JobPost> jobPosts = new AdminDaoImpl().getAllJobs(session);
			postedJobs = DTBConverter.getJobApplications(jobPosts);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return postedJobs;
	}

	

	@Override
	public List<JobApplication> getAllPendingJobs() {
		List<JobApplication> pendingJobs = null; 
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			List<CandidateApplication> candidateApplications = new AdminDaoImpl().getAllPendingApplications(session);
			pendingJobs = DTBConverter.getJobApplicants(candidateApplications);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return pendingJobs;
	}

	@Override
	public List<JobApplication> getAllAcceptedJobs() {
		List<JobApplication> postedJobs = null; 
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			List<CandidateApplication> jobPosts = new AdminDaoImpl().getAllAcceptedApplications(session);
			postedJobs = DTBConverter.getJobApplicants(jobPosts);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		return postedJobs;
	}
	
	@Override
	public List<Candidate> getAllUsers() {
		List<Candidate> candidates = new ArrayList<Candidate>();
		Session session = null;
		try {
			session = this.sessionFactory.openSession();
			List<Candidates> candidatesList = new AdminDaoImpl().getAllCandidates(session);
			if(CollectionUtils.isNotEmpty(candidatesList)) {
				for(Candidates c: candidatesList) {
					Candidate candidate = DTBConverter.getCandidateBasic(c);
					candidates.add(candidate);
				}
			}
			
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			CommonUtils.closeSession(session);
		}
		
		return candidates;
	}

	@Override
	public String sendToAll(AdminMail mail) {
		if(mail == null) {
			return null;
		}
		List<Candidate> candidates = new ArrayList<Candidate>();
		Session session = null;
		String result = JobzConstants.RESPONSE_OK;
		try {
			session = this.sessionFactory.openSession();
			AdminDaoImpl adminDaoImpl = new AdminDaoImpl();
			List<Candidates> candidatesList = null;
			if(mail.getType() == null || StringUtils.equals("All", mail.getType())) {
				candidatesList = adminDaoImpl.getAllCandidates(session);
			} else {
				candidatesList = adminDaoImpl.getAllCandidates(session, mail.getType());
			}
			if(CollectionUtils.isNotEmpty(candidatesList)) {
				for(Candidates c: candidatesList) {
					Candidate candidate = DTBConverter.getCandidateBasic(c);
					candidates.add(candidate);
				}
			}
			JobzMailUtil mailer = new JobzMailUtil(JobzConstants.MAIL_TYPE_GENERIC);
			mailer.setMailSubject(mail.getSubject());
			mailer.setMessageText(mail.getMessage());
			mailer.setCandidates(candidates);
			executor.execute(mailer);
		} catch (Exception e) {
			LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
			result = JobzConstants.ERROR_IN_PROCESSING;
		} finally {
			CommonUtils.closeSession(session);
		}
		return result;
	}

}
