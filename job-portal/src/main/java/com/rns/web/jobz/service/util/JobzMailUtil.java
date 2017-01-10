package com.rns.web.jobz.service.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.bo.domain.JobApplication;

public class JobzMailUtil implements Runnable, JobzConstants {

	private static final String READ_RECEIPT_MAIL = "talnoterns@gmail.com";
	private static final String MAIL_ID = "contact@talnote.com";
	// private static final String MAIL_ID = "support@talnote.com";
	// private static final String MAIL_ID = "ajinkyashiva@gmail.com";
	private static final String MAIL_AUTH = "true";
	private static final String MAIL_HOST = "support.tiffeat.com";
	// private static final String MAIL_HOST = "smtp.gmail.com";
	// private static final String MAIL_HOST = "smtp-relay.gmail.com";
	private static final String MAIL_PORT = "25";
	private static final String MAIL_PASSWORD = "contact2016";
	// private static final String MAIL_PASSWORD = "support2016";

	private String type;
	private Candidate candidate;
	private JobApplication jobApplication;
	private List<Candidate> candidates;
	private String messageText;
	private String mailSubject;

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

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
			if (CollectionUtils.isNotEmpty(candidates)) {
				for (Candidate c : candidates) {
					candidate = c;
					if(StringUtils.isBlank(c.getEmail())) {
						continue;
					}
					if (c.getCompatibility() != null && BigDecimal.TEN.compareTo(c.getCompatibility()) > 0) {
						continue;
					}
					if (jobApplication != null) {
						jobApplication.setCompatibility(c.getCompatibility());
					}
					//System.out.println("Considering :" + c.getEmail());
					prepareMailContent(message);
					Transport.send(message);
				}
			} else {
				prepareMailContent(message);
				Transport.send(message);
				if (candidate != null && StringUtils.isNotEmpty(candidate.getEmail())) {
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
		props.put("mail.smtp.socketFactory.port", "465"); //PROD
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //PROD
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
			boolean attachCv = false;
			String result = readMailContent(message);
			List<Candidate> mailchain = new ArrayList<Candidate>();
			if (candidate != null) {
				result = StringUtils.replace(result, "{name}", CommonUtils.getStringValue(candidate.getName()));
				result = StringUtils.replace(result, "{candidateEmail}", CommonUtils.getStringValue(candidate.getEmail()));
				result = StringUtils.replace(result, "{candidatePhone}", CommonUtils.getStringValue(candidate.getPhone()));
				result = StringUtils.replace(result, "{candidateName}", CommonUtils.getStringValue(candidate.getName()));
				result = StringUtils.replace(result, "{code}", CommonUtils.getStringValue(candidate.getActivationCode()));
				result = StringUtils.replace(result, "{designation}", CommonUtils.getStringValue((candidate.getDesignation())));
				result = StringUtils.replace(result, "{company}", CommonUtils.getStringValue(candidate.getCompany()));
				result = StringUtils.replace(result, "{skills}", CommonUtils.getSkills(candidate.getJobSkills()));
				if (candidate.getExperience() != null) {
					result = StringUtils.replace(result, "{experience}", candidate.getExperience().toString());
				} else {
					result = StringUtils.replace(result, "{experience}", "");
				}
				result = StringUtils.replace(result, "{password}", CommonUtils.getStringValue(candidate.getPassword()));
				if (MAIL_TYPE_ACTIVATION.equals(type)) {
					result = StringUtils.replace(result, "{link}", prepareActivationMailContent());
				}
				if (Arrays.asList(SEEKER_MAIL_LIST).contains(type)) {
					mailchain.add(candidate);
				}
				if (candidate.getResume() != null && jobApplication != null && jobApplication.isAttachCv()) {
					/*result = StringUtils.replace(result, "{resume}", "Also, please find attached the Resume of the applicant.");
					attachCv = true;*/
					result = StringUtils.replace(result, "{resume}", "<a href='" + ROOT_URL + "download/" + candidate.getEmail() + "/" + jobApplication.getId() +"/" + jobApplication.getPostedBy().getEmail() + "' ><button type='button' class='myButton'>Download Resume</button></a>");
				} else {
					result = StringUtils.replace(result, "{resume}", "");
				}
			}
			if (jobApplication != null) {
				Candidate poster = jobApplication.getPostedBy();
				if (Arrays.asList(POSTER_MAIL_LIST).contains(type)) {
					mailchain.add(poster);
					message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("talnoterns@gmail.com"));
				}
				if (poster != null) {
					result = StringUtils.replace(result, "{posterName}", CommonUtils.getStringValue(poster.getName()));
					result = StringUtils.replace(result, "{posterEmail}", CommonUtils.getStringValue(poster.getEmail()));
					result = StringUtils.replace(result, "{posterPhone}", CommonUtils.getStringValue(poster.getPhone()));
				}
				result = StringUtils.replace(result, "{jobCompany}", CommonUtils.getStringValue(jobApplication.getCompanyName()));
				result = StringUtils.replace(result, "{jobTitle}", CommonUtils.getStringValue(jobApplication.getJobTitle()));
				if (jobApplication.getCompatibility() != null) {
					result = StringUtils.replace(result, "{compatibility}", jobApplication.getCompatibility().toString());
				} else {
					result = StringUtils.replace(result, "{compatibility}", "");
				}
				if (jobApplication.getMinExperience() != null && jobApplication.getMaxExperience() != null) {
					result = StringUtils.replace(result, "{jobExperience}", jobApplication.getMinExperience().toString() + " - " + jobApplication.getMaxExperience());
				} else {
					result = StringUtils.replace(result, "{jobExperience}", "");
				}
				if (jobApplication.getId() != null) {
					result = StringUtils.replace(result, "{jobId}", jobApplication.getId().toString());
				} else {
					result = StringUtils.replace(result, "{jobId}", "");
				}
				result = StringUtils.replace(result, "{skillsRequired}", CommonUtils.getSkills(jobApplication.getSkillsRequired()));
				result = StringUtils.replace(result, "{unsubscribeLink}", prepareUnsubscribeMailContent());
				/*result = StringUtils.replace(result, "{logoPath}", prepareLogoLink());*/
				if(MAIL_TYPE_NEW_JOB.equals(type)) {
					message.setSubject(jobApplication.getJobTitle() + " required at " + jobApplication.getCompanyName());
				}
				if(MAIL_TYPE_NEW_JOB_POC.equals(type)) {
					message.setHeader("Disposition-Notification-To", READ_RECEIPT_MAIL);
					message.addHeader("Disposition-Notification-To", READ_RECEIPT_MAIL);
				}
			}
			if (StringUtils.isNotBlank(messageText)) {
				result = StringUtils.replace(result, "{message}", messageText);
			}
			message.setContent(result, "text/html");
			/*if(attachCv) {
				try {
					attachCv(message, candidate, result);
				} catch (IOException e) {
					LoggingUtil.logMessage(ExceptionUtils.getStackTrace(e));
				}
			}*/
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

	private String prepareLogoLink() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ROOT_URL).append("getLogo/").append(jobApplication.getId());
		return buffer.toString();
	}

	private String getEmails(List<Candidate> users) {
		if (CollectionUtils.isEmpty(users)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Candidate user : users) {
			if (StringUtils.isEmpty(user.getEmail())) {
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
		builder.append(ROOT_URL_ACTIVATION).append("#?").append(ACTIVATION_URL_VAR).append("=").append(candidate.getActivationCode()).append("&").append(ACTIVATION_USER_VAR).append("=")
				.append(candidate.getEmail());
		return builder.toString();
	}
	
	private String prepareUnsubscribeMailContent() {
		if(jobApplication == null || jobApplication.getPostedBy() == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(ROOT_URL_UNSUBSCRIBE).append("#?").append(UNSUBSCRIBE_ID_VAR).append("=").append(jobApplication.getId()).append("&").append(UNSUBSCRIBE_EMAIL_VAR).append("=")
				.append(jobApplication.getPostedBy().getEmail());
		return builder.toString();
	}

	private String readMailContent(Message message) throws FileNotFoundException, MessagingException {
		String contentPath = "";
		contentPath = "email/" + MAIL_TEMPLATES.get(type);
		if (StringUtils.equals(MAIL_TYPE_GENERIC, type) && StringUtils.isNotBlank(mailSubject)) {
			message.setSubject(mailSubject);
		} else {
			message.setSubject(MAIL_SUBJECTS.get(type));
		}
		return CommonUtils.readFile(contentPath);
	}

	private void attachCv(Message message, Candidate candidate, String result) throws MessagingException, IOException {
		Multipart mp = new MimeMultipart();
		BodyPart fileBody = new MimeBodyPart();
		DataSource source = new FileDataSource(candidate.getResume());
		fileBody.setDataHandler(new DataHandler(source));
		fileBody.setFileName(candidate.getFilePath());
		BodyPart messsageBody = new MimeBodyPart();
		messsageBody.setText(result);
		messsageBody.setContent(result, "text/html");
		mp.addBodyPart(fileBody);
		mp.addBodyPart(messsageBody);
		message.setContent(mp);
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
			put(MAIL_TYPE_FORGOT_PWD, "forgot_password.html");
			put(MAIL_TYPE_POSTER_REJECTED, "poster_rejected.html");
			put(MAIL_TYPE_NEW_JOB_POC, "new_job_poc.html");
			put(MAIL_TYPE_GENERIC, "generic_mail.html");
			put(MAIL_TYPE_RESUME_DOWNLOAD, "resume_download.html");
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
			put(MAIL_TYPE_NEW_JOB, "{designation} required at {company}");
			put(MAIL_TYPE_FORGOT_PWD, "Talnote Forgot password");
			put(MAIL_TYPE_POSTER_REJECTED, "Job Application Rejected");
			put(MAIL_TYPE_NEW_JOB_POC, "Job posted for you!");
			put(MAIL_TYPE_RESUME_DOWNLOAD, "Your resume was downloaded!");
		}
	});

}
