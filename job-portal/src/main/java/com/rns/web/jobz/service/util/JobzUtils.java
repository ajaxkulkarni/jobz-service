package com.rns.web.jobz.service.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;
import com.rns.web.jobz.service.bo.domain.JobSkill;
import com.rns.web.jobz.service.bo.domain.Qualification;

public class JobzUtils {
	
	public static BigDecimal calculateCompatibility(Candidate currentCandidate, JobApplication jobApplication) {
		
		if(checkDiffSector(currentCandidate, jobApplication)) {
			//TODO: To show everything to evryone
			return new BigDecimal(0);
		}
		
		double criteria = 0;
		double matchCount = 0;
		
		for(JobSkill skill : jobApplication.getSkillsRequired()) {
			criteria++;
			for(JobSkill candidateSkill : currentCandidate.getJobSkills()) {
				if(StringUtils.equalsIgnoreCase(skill.getName(), candidateSkill.getName())) {
					matchCount++;
				}
			}
		}
		
		/*for(Qualification education : jobApplication.getEducationRequired()) {
			criteria++;
			for(Qualification candidateQualification : currentCandidate.getEducations()) {
				if(StringUtils.equalsIgnoreCase(education.getName(), candidateQualification.getName())) {
					matchCount++;
				}
			}
		}*/
		
		/*if(jobApplication.getExperience() != null) {
			criteria = criteria + jobApplication.getExperience().doubleValue();
		}*/
		if(jobApplication.getMinExperience() != null || jobApplication.getMaxExperience() != null) {
			criteria = criteria + 2;
		}
		matchCount = matchCount + getExperiencePts(jobApplication, currentCandidate);
		double compatibility = (matchCount/ criteria)*100;
		BigDecimal compatScore = new BigDecimal(Math.ceil(compatibility));
		if(compatScore!= null && BigDecimal.TEN.compareTo(compatScore) > 0) {
			//TODO: Temporary change. To show everything to everyone
			return BigDecimal.TEN;
		}
		return compatScore;
	}

	private static boolean checkDiffSector(Candidate currentCandidate, JobApplication jobApplication) {
		if(currentCandidate.getSector()== null || currentCandidate.getSector().getId() == null) {
			if(jobApplication.getSector() == null || jobApplication.getSector().getId() == null) {
				return false;
			} else {
				return true;
			}
		} else if (jobApplication.getSector() == null || jobApplication.getSector().getId() == null) {
			return false;
		}
		return currentCandidate.getSector().getId().intValue() != jobApplication.getSector().getId().intValue();
	}
	
	private static double getExperiencePts(JobApplication application, Candidate currentCandidate) {
		if(application.getMinExperience() == null && application.getMaxExperience() == null) {
			return 0;
		}
		if(currentCandidate.getExperience() == null) {
			return 0;
		}
		
		double points = 0;
		if(application.getMinExperience() != null && currentCandidate.getExperience().compareTo(application.getMinExperience()) >= 0) {
			points++;
		}
		
		if(application.getMaxExperience() != null && currentCandidate.getExperience().compareTo(application.getMaxExperience()) <= 0) {
			points++;
		}
		
		if(application.getMinExperience() != null && currentCandidate.getExperience().compareTo(application.getMinExperience().subtract(new BigDecimal(2))) < 0) {
			points--;
		}
		
		if(application.getMaxExperience() != null && currentCandidate.getExperience().compareTo(application.getMaxExperience().add(new BigDecimal(2))) > 0) {
			points--;
		}
		
		return points;
		
		/*if(application.getExperience().compareTo(currentCandidate.getExperience()) == 0 && application.getExperience().compareTo(BigDecimal.ZERO) != 0) {
			return application.getExperience().doubleValue();
		}
		double diff = Math.abs(application.getExperience().subtract(currentCandidate.getExperience()).doubleValue());
		if(application.getExperience().doubleValue() < diff) {
			return 0;
		}
		
		return application.getExperience().doubleValue() - diff;*/
	}
	
	public static List<JobApplication> sortByCompatibility(List<JobApplication> applications) {
		if(CollectionUtils.isEmpty(applications)) {
			return applications;
		}
		Collections.sort(applications, new Comparator<JobApplication>() {

			public int compare(JobApplication j1, JobApplication j2) {
				if(j1.getCompatibility() == null) {
					return -1;
				}
				if(j2.getCompatibility() == null) {
					return 1;
				}
				return j2.getCompatibility().compareTo(j1.getCompatibility());
			}
			
		});
		return applications;
	}
	
	public static List<Candidate> sortByCompatibilityCandidates(List<Candidate> applications) {
		if(CollectionUtils.isEmpty(applications)) {
			return applications;
		}
		Collections.sort(applications, new Comparator<Candidate>() {

			public int compare(Candidate j1, Candidate j2) {
				if(j1.getCompatibility() == null) {
					return -1;
				}
				if(j2.getCompatibility() == null) {
					return 1;
				}
				return j2.getCompatibility().compareTo(j1.getCompatibility());
			}
			
		});
		return applications;
	}
	
	public static List<JobApplication> sortById(List<JobApplication> applications) {
		if(CollectionUtils.isEmpty(applications)) {
			return applications;
		}
		Collections.sort(applications, new Comparator<JobApplication>() {

			public int compare(JobApplication j1, JobApplication j2) {
				if(j1.getId() == null) {
					return -1;
				}
				if(j2.getId() == null) {
					return 1;
				}
				return (j2.getId().intValue() > j1.getId().intValue()) ? 1: -1 ;
			}
			
		});
		return applications;
	}

	public static String generateActivationCode(Candidate candidate) {
		if(candidate == null) {
			return "";
		}
		return StringUtils.substring(candidate.getEmail(), 0, 1) + new Random().nextInt(10000);
	}

}
