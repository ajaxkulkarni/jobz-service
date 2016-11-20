package com.rns.web.jobz.service.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface JobzConstants {
	
	//Dev
	String ROOT_PATH = "F:/Resoneuronance/JobPortal/Uploads/";
	
	//Prod
	
	
	String RESPONSE_OK = "OK";
	String ERROR_INVALID_LOGIN_DETAILS = "Invalid login details!";
	String ERROR_INCOMPLETE_JOB_DETAILS = "Incomplete job details!";
	String ERROR_EMAIL_EXISTS = "Email already exists!";
	String ERROR_INCOMPLETE_DETAILS = "Incomplete candidate details!";
	String ERROR_IN_PROCESSING = "Error in processing..";
	String ERROR_INVALID_ACTIVATION_CODE = "Invalid activation code. Please try again.";
	String ERROR_EMAIL_DOES_NOT_EXIST = "Email address does not exist!";
	String ERROR_INVALID_FILE = "Invalid/No file selected";



	String YES = "Y";
	
	Map<Integer, String> SECTORS = Collections.unmodifiableMap(new HashMap<Integer, String>(){{ 
        put(1, "IT");
        put(2, "Telecommunication");
        put(3, "Mechanical");
        put(4, "Industrial");
        put(5,"Media and publishing");
        put(6,"Teaching");
        put(7,"Sales and supply chain");
        put(8,"Creative arts and design");
        put(9,"Other");
        put(10,"Medical");
        put(11,"Construction");
    }});
	
	
	String ROOT_URL = "http://www.talnote.com/activation.html";
	String ACTIVATION_URL_VAR = "activationCode";
	String ACTIVATION_USER_VAR = "activationUser";
	String INACTIVE = "I";
	String ACTIVE = "A";
	String NO = "N";
	
	String MAIL_TYPE_ACTIVATION = "activationMail";
	String MAIL_TYPE_REGISTRATION = "registrationMail";
	String MAIL_TYPE_GOT_SEEKER_CONTACT = "seekerContactReceived";
	String MAIL_TYPE_GOT_POSTER_CONTACT = "posterContactReceived";
	String MAIL_TYPE_SEEKER_APPLY = "seekerInterested";
	String MAIL_TYPE_POSTER_APPLY = "posterInterested";
	String MAIL_TYPE_NEW_JOB = "newJob";
	String MAIL_TYPE_FORGOT_PWD = "forgotPassword";
	String MAIL_TYPE_POSTER_REJECTED = "posterRejected";
	
	String[] SEEKER_MAIL_LIST = {MAIL_TYPE_POSTER_APPLY, MAIL_TYPE_GOT_POSTER_CONTACT, MAIL_TYPE_NEW_JOB, MAIL_TYPE_ACTIVATION, 
			MAIL_TYPE_REGISTRATION, MAIL_TYPE_FORGOT_PWD, MAIL_TYPE_POSTER_REJECTED};
	String[] POSTER_MAIL_LIST = {MAIL_TYPE_SEEKER_APPLY, MAIL_TYPE_GOT_SEEKER_CONTACT};

	

}
