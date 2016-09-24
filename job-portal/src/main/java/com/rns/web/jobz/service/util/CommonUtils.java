package com.rns.web.jobz.service.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;

public class CommonUtils {
	
	public static void closeSession(Session session) {
		if(session == null || !session.isOpen())  {
			return;
		}
		session.close();
		System.out.println("Session closed!");
	}

	public static String convertDate(Date date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		} catch (Exception e) {
		}
		return null;
	}
	
}
