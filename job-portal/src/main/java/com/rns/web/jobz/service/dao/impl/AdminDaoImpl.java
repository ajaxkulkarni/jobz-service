package com.rns.web.jobz.service.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.Candidates;
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
	
	public List<Candidates> getAllCandidates(Session session) {
		Query query = session.createQuery("from Candidates order by ID DESC");
		return query.list();
	}

	public List<Candidates> getAllCandidates(Session session, String type) {
		Query query = session.createQuery("from Candidates where type=:candidate_type order by ID DESC");
		query.setString("candidate_type", type);
		return query.list();
	}

}
