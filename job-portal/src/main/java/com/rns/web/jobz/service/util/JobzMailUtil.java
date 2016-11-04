package com.rns.web.jobz.service.util;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;

public class JobzMailUtil implements Runnable, JobzConstants {

	private static final String MAIL_ID = "contact@talnote.com";
	//private static final String MAIL_ID = "support@talnote.com";
	//private static final String MAIL_ID = "ajinkyashiva@gmail.com";
	private static final String MAIL_AUTH = "true";
	private static final String MAIL_HOST = "support.tiffeat.com";
	//private static final String MAIL_HOST = "smtp.gmail.com";
	//private static final String MAIL_HOST = "smtp-relay.gmail.com";
	private static final String MAIL_PORT = "25";
	private static final String MAIL_PASSWORD = "contact_talnote2016";
	//private static final String MAIL_PASSWORD = "support2016";

	private String type;
	private Candidate candidate;
	private JobApplication jobApplication;
	private List<Candidate> candidates;
	
	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public JobzMailUtil(String mailType) {
		this.type = mailType;
		this.candidates = new ArrayList<Candidate>();
	}
	
	public JobzMailUtil() {
	}

	public void sendMail() {

		Session session = prepareMailSession();

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(MAIL_ID, "Talnote"));
			if(CollectionUtils.isNotEmpty(candidates)) {
				for(Candidate c: candidates) {
					candidate = c;
					if(BigDecimal.TEN.compareTo(c.getCompatibility()) > 0) {
						continue;
					}
					jobApplication.setCompatibility(c.getCompatibility());
					prepareMailContent(message);
					Transport.send(message);
				}
			} else {
				prepareMailContent(message);
				Transport.send(message);
				if(StringUtils.isNotEmpty(candidate.getEmail())) {
					LoggingUtil.logMessage("Mail " + type + " sent to :" + candidate.getEmail());
				}
			}
			

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	private static Session prepareMailSession() {
		Properties props = new Properties();

		props.put("mail.smtp.auth", MAIL_AUTH);
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", MAIL_HOST);
		props.put("mail.smtp.port", MAIL_PORT);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(MAIL_ID, MAIL_PASSWORD);
			}
		});
		return session;
	}

	private String prepareMailContent(Message message) {

		try {

			String result = readMailContent(message);
			List<Candidate> mailchain = new ArrayList<Candidate>();
			if(candidate != null) {
				result = StringUtils.replace(result, "{name}", CommonUtils.getStringValue(candidate.getName()));
				result = StringUtils.replace(result, "{candidateEmail}", CommonUtils.getStringValue(candidate.getEmail()));
				result = StringUtils.replace(result, "{candidatePhone}", CommonUtils.getStringValue(candidate.getPhone()));
				result = StringUtils.replace(result, "{candidateName}", CommonUtils.getStringValue(candidate.getName()));
				result = StringUtils.replace(result, "{code}", CommonUtils.getStringValue(candidate.getActivationCode()));
				result = StringUtils.replace(result, "{designation}", CommonUtils.getStringValue((candidate.getDesignation())));
				result = StringUtils.replace(result, "{company}", CommonUtils.getStringValue(candidate.getCompany()));
				if(candidate.getExperience() != null) {
					result = StringUtils.replace(result, "{experience}", candidate.getExperience().toString());
				} else {
					result = StringUtils.replace(result, "{experience}", "");
				}
				result = StringUtils.replace(result, "{password}", CommonUtils.getStringValue(candidate.getPassword()));
				if(MAIL_TYPE_ACTIVATION.equals(type)) {
					result = StringUtils.replace(result, "{link}", prepareActivationMailContent());
				}
				if(Arrays.asList(SEEKER_MAIL_LIST).contains(type)) {
					mailchain.add(candidate);
				}
			}
			if(jobApplication != null) {
				Candidate poster = jobApplication.getPostedBy();
				if(Arrays.asList(POSTER_MAIL_LIST).contains(type)) {
					mailchain.add(poster);
				}
				if(poster != null) {
					result = StringUtils.replace(result, "{posterName}", CommonUtils.getStringValue(poster.getName()));
					result = StringUtils.replace(result, "{posterEmail}", CommonUtils.getStringValue(poster.getEmail()));
					result = StringUtils.replace(result, "{posterPhone}", CommonUtils.getStringValue(poster.getPhone()));
				}
				result = StringUtils.replace(result, "{jobCompany}", CommonUtils.getStringValue(jobApplication.getCompanyName()));
				result = StringUtils.replace(result, "{jobTitle}", CommonUtils.getStringValue(jobApplication.getJobTitle()));
				if(jobApplication.getCompatibility() != null) {
					result = StringUtils.replace(result, "{compatibility}", jobApplication.getCompatibility().toString());
				} else {
					result = StringUtils.replace(result, "{compatibility}", "");
				}
				if(jobApplication.getMinExperience() != null && jobApplication.getMaxExperience() != null) {
					result = StringUtils.replace(result, "{jobExperience}", jobApplication.getMinExperience().toString() + " - " + jobApplication.getMaxExperience());
				} else {
					result = StringUtils.replace(result, "{jobExperience}", "");
				}
			}
			message.setContent(result, "text/html");
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmails(mailchain)));
			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 

		return "";
	}

	private String getEmails(List<Candidate> users) {
		if (CollectionUtils.isEmpty(users)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Candidate user : users) {
			if(StringUtils.isEmpty(user.getEmail())) {
				continue;
			}
			builder.append(user.getEmail()).append(",");
		}
		return StringUtils.removeEnd(builder.toString(), ",");
	}

	

	public void run() {
		sendMail();
	}

	private String prepareActivationMailContent() {
		StringBuilder builder = new StringBuilder();
		builder.append(ROOT_URL).append("#?").append(ACTIVATION_URL_VAR).append("=").append(candidate.getActivationCode()).
		append("&").append(ACTIVATION_USER_VAR).append("=").append(candidate.getEmail());
		return builder.toString();
	}

	private String readMailContent(Message message) throws FileNotFoundException, MessagingException {
		String contentPath = "";
		contentPath = "email/" + MAIL_TEMPLATES.get(type);
		message.setSubject(MAIL_SUBJECTS.get(type));
		return CommonUtils.readFile(contentPath);
	}

	public Candidate getCandidate() {
		return candidate;
	}


	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public JobApplication getJobApplication() {
		return jobApplication;
	}


	public void setJobApplication(JobApplication jobApplication) {
		this.jobApplication = jobApplication;
	}

	private static Map<String, String> MAIL_TEMPLATES = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_ACTIVATION, "activation_mail.html");
			put(MAIL_TYPE_REGISTRATION, "registration_mail.html");
			put(MAIL_TYPE_POSTER_APPLY, "poster_interest.html");
			put(MAIL_TYPE_SEEKER_APPLY, "seeker_interest.html");
			put(MAIL_TYPE_GOT_SEEKER_CONTACT, "seeker_contact_received.html");
			put(MAIL_TYPE_GOT_POSTER_CONTACT, "poster_contact_received.html");
			put(MAIL_TYPE_NEW_JOB, "new_job.html");
		}
	});

	private static Map<String, String> MAIL_SUBJECTS = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put(MAIL_TYPE_ACTIVATION, "Talnote account activation");
			put(MAIL_TYPE_REGISTRATION, "Thank you for choosing Talnote");
			put(MAIL_TYPE_POSTER_APPLY, "You have a new job request");
			put(MAIL_TYPE_SEEKER_APPLY, "You have a new candidate application");
			put(MAIL_TYPE_GOT_SEEKER_CONTACT, "Congrats! You've got a contact!");
			put(MAIL_TYPE_GOT_POSTER_CONTACT, "Congrats! You've got a contact!");
			put(MAIL_TYPE_NEW_JOB, "New Job application for you!");
		}
	});
	
}
