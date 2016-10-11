package com.rns.web.jobz.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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
	
	public static String readFile(String contentPath) throws FileNotFoundException {
		ClassLoader classLoader = new CommonUtils().getClass().getClassLoader();
		URL resource = classLoader.getResource(contentPath);
		File file = new File(resource.getFile());
		Scanner scanner = new Scanner(file);
		StringBuilder result = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.append(line).append("\n");
		}

		scanner.close();
		return result.toString();
	}
	
}
