package com.rns.web.jobz.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.jobz.service.bo.domain.Candidate;
import com.rns.web.jobz.service.dao.domain.Candidates;

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
	
	public static String getStringValue(String value) {
		return StringUtils.isNotEmpty(value) ? value : "";
	}

	public static String getFilePath(Candidates candidates) {
		StringBuilder builder = new StringBuilder();
		builder.append(JobzConstants.ROOT_PATH).append(candidates.getId());
		return builder.toString();
	}

	public static String getFileName(String filePath) {
		String[] tokens = StringUtils.split(filePath, ".");
		if(tokens == null || tokens.length == 0) {
			return null;
		}
		return tokens[tokens.length - 1];
	}
	
}
