package com.rns.web.jobz.service.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;
import com.rns.web.jobz.service.dao.domain.Candidates;
import com.rns.web.jobz.service.dao.domain.Education;
import com.rns.web.jobz.service.dao.domain.JobPost;
import com.rns.web.jobz.service.dao.domain.Skills;

public class BTDConverter {

	public static Set<Education> getEducations(List<Qualification> qualifications) {
		if (CollectionUtils.isEmpty(qualifications)) {
			return null;
		}
		Set<Education> educations = new HashSet<Education>(0);
		for (Qualification qualification : qualifications) {
			Education education = getEducation(qualification);
			if (education == null) {
				continue;
			}
			educations.add(education);
		}
		return educations;
	}

	private static Education getEducation(Qualification qualification) {
		if (qualification == null) {
			return null;
		}
		Education education = new Education();
		education.setName(qualification.getName());
		return education;
	}

	public static Set<Skills> getSkills(List<JobSkill> jobSkills) {
		if (CollectionUtils.isEmpty(jobSkills)) {
			return null;
		}
		Set<Skills> skills = new HashSet<Skills>(0);
		for (JobSkill jobSkill : jobSkills) {
			Skills skill = getSkill(jobSkill);
			if (skill == null) {
				continue;
			}
			skills.add(skill);
		}
		return skills;
	}

	private static Skills getSkill(JobSkill jobSkill) {
		if (jobSkill == null) {
			return null;
		}
		Skills skill = new Skills();
		skill.setName(jobSkill.getName());
		return skill;
	}

	public static Candidates getCandidates(Candidate candidate) {
		Candidates candidates = new Candidates();
		setCandidates(candidate, candidates);
		return candidates;
	}

	public static void setCandidates(Candidate candidate, Candidates candidates) {
		candidates.setName(candidate.getName());
		candidates.setCompany(candidate.getCompany());
		candidates.setDescription(candidate.getDescription());
		candidates.setDesignation(candidate.getDesignation());
		candidates.setExperience(candidate.getExperience());
		candidates.setPassword(candidate.getPassword());
		candidates.setEmail(candidate.getEmail());
		candidates.setPhone(candidate.getPhone());
		candidates.setType(candidate.getType());
		if(candidate.getSector() != null) {
			candidates.setSector(candidate.getSector().getId());
		}
		// candidates.setEducation(BTDConverter.getEducations(candidate.getEducations()));
		// candidates.setSkills(BTDConverter.getSkills(candidate.getJobSkills()));
	}

	public static JobPost getJobPost(JobApplication application) {
		JobPost post = new JobPost();
		setJobPost(application, post);
		// post.setEducation(getEducations(application.getEducationRequired()));
		// post.setSkills(getSkills(application.getSkillsRequired()));
		return post;
	}

	public static void setJobPost(JobApplication application, JobPost post) {
		post.setCompanyName(application.getCompanyName());
		post.setDescription(application.getDescription());
		post.setMinExperience(application.getMinExperience());
		post.setMaxExperience(application.getMaxExperience());
		if (application.getExpiryDate() != null) {
			post.setExpiryDate(application.getExpiryDate());
		}
		post.setLocation(application.getLocation());
		post.setMaxApplicants(application.getMaxApplicants());
		if (application.getPostedDate() != null) {
			post.setPostedDate(application.getPostedDate());
		}
		post.setSalary(application.getSalary());
		post.setJobTitle(application.getJobTitle());
		post.setType(application.getType());
		if (application.getSector() != null) {
			post.setSector(application.getSector().getId());
		}
		if(isPoc(application)) {
			post.setPocEmail(application.getPoc().getEmail());
			post.setPocPhone(application.getPoc().getPhone());
		}
	}

	public static boolean isPoc(JobApplication application) {
		return application.getPoc() != null && StringUtils.isNotBlank(application.getPoc().getEmail());
	}

}
