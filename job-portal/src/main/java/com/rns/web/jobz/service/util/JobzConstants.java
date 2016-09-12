package com.rns.web.jobz.service.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface JobzConstants {
	
	String ERROR_INVALID_LOGIN_DETAILS = "Invalid login details!";
	String ERROR_INCOMPLETE_JOB_DETAILS = "Incomplete job details!";
	String ERROR_EMAIL_EXISTS = "Email already exists!";
	String RESPONSE_OK = "OK";
	String ERROR_INCOMPLETE_DETAILS = "Incomplete candidate details!";
	String ERROR_IN_PROCESSING = "Error in processing..";

	String YES = "Y";
	
	Map<Integer, String> SECTORS = Collections.unmodifiableMap(new HashMap<Integer, String>(){{ 
        put(1, "IT");
        put(2, "Telecommunication");
        put(3, "Mechanical");
        put(4, "Industrial");
    }});
	

}
