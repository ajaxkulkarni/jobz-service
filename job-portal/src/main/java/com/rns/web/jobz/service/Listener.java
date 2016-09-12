package com.rns.web.jobz.service;

import javax.ws.rs.ext.Provider;

import com.rns.web.jobz.service.util.LoggingUtil;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

@Provider
public class Listener implements AbstractResourceModelListener {

	@Override
	public void onLoaded(AbstractResourceModelContext modelContext) {
		LoggingUtil.logMessage("##### service initiated ..");
	}
}
