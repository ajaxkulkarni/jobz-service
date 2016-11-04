package com.rns.web.jobz.service.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.rns.web.jobz.service.dao.domain.CandidateApplication;
import com.rns.web.jobz.service.dao.domain.Candidates;
import com.rns.web.jobz.service.dao.domain.Education;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.domain.Skills;
import com.rns.web.jobz.service.util.JobzConstants;

public class CandidateDaoImpl {

	public void addCandidate(Candidates candidates, Session session) {
		session.save(candidates);
	}

	public Education getEducationByName(String name, Session session) {
		Query query = session.createQuery("from Education where name=:edu_name");
		query.setString("edu_name", name);
		List<Education> educations = query.list();
		if (CollectionUtils.isEmpty(educations)) {
			return null;
		}
		return educations.get(0);
	}

	public Skills getSkillByName(String name, Session session) {
		Query query = session.createQuery("from Skills where name=:skill_name");
		query.setString("skill_name", name);
		List<Skills> skills = query.list();
		if (CollectionUtils.isEmpty(skills)) {
			return null;
		}
		return skills.get(0);
	}

	public int addEducation(Education education, Session session) {
		session.persist(education);
		return education.getId();
	}

	public int addSkill(Skills skills, Session session) {
		session.persist(skills);
		return skills.getId();
	}

	public Candidates getCandidateByEmail(String email, Session session) {
		Query query = session.createQuery("from Candidates where email=:email_id");
		query.setString("email_id", email);
		List<Candidates> candidate = query.list();
		if (CollectionUtils.isEmpty(candidate)) {
			return null;
		}
		return candidate.get(0);
	}

	public List<JobPost> getAllAvailableJobs(int candidateId, Session session) {
		Query query = session.createQuery("from JobPost where postedBy.id!=:candidate_id AND expiryDate>=:cur_date order by ID DESC");
		query.setInteger("candidate_id", candidateId);
		query.setDate("cur_date", new Date());
		return query.list();
	}

	public List<Skills> getSkillsByname(String name, Session session) {
		Query query = session.createQuery("from Skills where name LIKE:skill_name");
		query.setParameter("skill_name", "%" + name + "%");
		return query.list();
	}
	
	public List<Education> getEducationsByName(String name, Session session) {
		Query query = session.createQuery("from Education where name LIKE:skill_name");
		query.setParameter("skill_name", "%" + name + "%");
		return query.list();
	}

	public CandidateApplication getApplication(Integer id, String email, Session session) {
		Query query = session.createQuery("from CandidateApplication where jobPost.id=:post_id AND candidates.email=:email_id");
		query.setInteger("post_id", id);
		query.setString("email_id", email);
		List<CandidateApplication> applications = query.list();
		if (CollectionUtils.isEmpty(applications)) {
			return null;
		}
		return applications.get(0);
	}

	public List<Candidates> getAllCandidates(Session session) {
		Query query = session.createQuery("from Candidates where type IS NULL OR type!=:poster AND (status=:active OR status IS NULL)");
		query.setString("active", JobzConstants.ACTIVE);
		query.setString("poster", "Poster");
		return query.list();
	}

	public JobPost getJobApplication(Integer id, Session session) {
		Query query = session.createQuery("from JobPost where id=:post_id");
		query.setInteger("post_id", id);
		List<JobPost> applications = query.list();
		if (CollectionUtils.isEmpty(applications)) {
			return null;
		}
		return applications.get(0);
	}
	
}
