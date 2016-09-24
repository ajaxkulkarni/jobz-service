package com.rns.web.jobz.service.bo.impl;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.rns.web.jobz.service.bo.api.AdminBo;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.impl.AdminDaoImpl;
import com.rns.web.jobz.service.util.CommonUtils;
import com.rns.web.jobz.service.util.DTBConverter;
import com.rns.web.jobz.service.util.LoggingUtil;

public class AdminBoImpl implements AdminBo {
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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

}
