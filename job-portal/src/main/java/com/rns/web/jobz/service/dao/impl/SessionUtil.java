package com.rns.web.jobz.service.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionUtil {

	private static SessionUtil instance = new SessionUtil();
	private SessionFactory sessionFactory;

	public static SessionUtil getInstance() {
		return instance;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private SessionUtil() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");

		sessionFactory = configuration.buildSessionFactory();
	}

	public static Session getSession() {
		Session session = getInstance().sessionFactory.openSession();
		return session;
	}
}
