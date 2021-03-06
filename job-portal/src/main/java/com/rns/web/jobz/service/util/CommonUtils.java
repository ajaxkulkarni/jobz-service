package com.rns.web.jobz.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.rns.web.jobz.service.bo.domain.JobServiceResponse;
import com.rns.web.jobz.service.bo.domain.JobSkill;
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
		File file = getFile(contentPath);
		Scanner scanner = new Scanner(file);
		StringBuilder result = new StringBuilder();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.append(line).append("\n");
		}

		scanner.close();
		return result.toString();
	}

	public static File getFile(String contentPath) {
		ClassLoader classLoader = new CommonUtils().getClass().getClassLoader();
		URL resource = classLoader.getResource(contentPath);
		File file = new File(resource.getFile());
		return file;
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
	
	public static void checkForError(JobServiceResponse response) {
		if (!JobzConstants.RESPONSE_OK.equals(response.getResponseText())) {
			response.setStatus(-111);
		}
	}
	
	public static String getSkills(List<JobSkill> list) {
		if(CollectionUtils.isEmpty(list)) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for(JobSkill value: list) {
			if(StringUtils.isEmpty(value.getName())) {
				continue;
			}
			buffer.append(value.getName()).append(",");
		}
		return StringUtils.removeEnd(buffer.toString(), ",");
	}
	
}
