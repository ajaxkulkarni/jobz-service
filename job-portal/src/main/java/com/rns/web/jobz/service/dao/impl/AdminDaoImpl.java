package com.rns.web.jobz.service.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.util.JobzConstants;

public class AdminDaoImpl {
	
	public List<JobPost> getAllJobs(Session session) {
		Query query = session.createQuery("from JobPost order by ID DESC");
		return query.list();
	}
	
	public List<CandidateApplication> getAllPendingApplications(Session session) {
		Query query = session.createQuery("from CandidateApplication where interestShownByPoster IS NULL OR interestShownBySeeker IS NULL order by ID DESC");
		//query.setString("yes", null);
		return query.list();
	}
	
	public List<CandidateApplication> getAllAcceptedApplications(Session session) {
		Query query = session.createQuery("from CandidateApplication where interestShownByPoster=:yes AND interestShownBySeeker=:yes  order by ID DESC");
		query.setString("yes", JobzConstants.YES);
		return query.list();
	}

}
