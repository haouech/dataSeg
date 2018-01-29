package com.pfe.entities;

import org.semanticweb.owlapi.model.OWLClass;

import com.pfe.OntologyManager;

public class ActivityFactory {

	public ActivityFactory() {
		
	}
	
	@SuppressWarnings("deprecation")
	public Activity makeEquivalentActivity(OWLClass mClass, int startTime) {
		Activity result = new Activity(mClass.getIRI().getFragment());
		result.setStartTime(startTime);
		result.setAsserted(true);
		result.setSpecific(true);
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public Activity makePossibleActivity(OWLClass mClass, int startTime) {
		Activity result = new Activity(mClass.getIRI().getFragment());
		result.setStartTime(startTime);
		result.setAsserted(false);
		result.setSpecific(OntologyManager.getInstance().isSpecific(mClass));
		return result;
	}
}
