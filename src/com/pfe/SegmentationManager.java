package com.pfe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.pfe.entities.Window;

import com.pfe.entities.Activity;
import com.pfe.entities.ReasoningMode;

public class SegmentationManager {

	private OntologyManager ontologyManager;
	private TimeManager timeManager;
	private DataManager dataManager;
	
	public SegmentationManager() {
		ontologyManager = OntologyManager.getInstance();
		timeManager = TimeManager.getInstance();
		dataManager = DataManager.getInstance();
	}
	
	public void recognizeADL() {

		OWLClass classe = OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(
				"http://www.semanticweb.org/asma/ontologies/2018/0/Activities#Lunch" ));
		ontologyManager.isSpecific(classe);
		
		Logger.init();
		int i=0;
		Window initWindow = new Window(timeManager.getCurrentTime());
		Window currentWindow = initWindow ;	
		Map<Window,Set<Activity>> windowToActivitiesMap = new HashMap<>();
		currentWindow.setActive(true);
		while (timeManager.isStillRunning()) 
		{
			Set<Activity> activitiesForCurrentWindow = new HashSet<>();
			List<Activity> res = new ArrayList<>();
			while (timeManager.isStillRunning() && currentWindow.getEndTime() > timeManager.getCurrentTime())
			{
				//Lecture du dataSet 
				if(!dataManager.readLine(timeManager.getCurrentTime(), currentWindow)) {
					timeManager.advanceTime();
					continue;
				}
				if(timeManager.getCurrentTime() > 73167) {
					System.err.println();
				}
				if(ReasoningMode.on_sensor.equals(currentWindow.getReasoningMode()) 
						|| ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode())) {
					res = doOntologicalAR(currentWindow);
				} else if(ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode()) 
						&& timeManager.isInterval()) {
					res = doOntologicalAR(currentWindow);
				}
				timeManager.advanceTime();
				
			} // End of window
			activitiesForCurrentWindow.addAll(res);
			if(ReasoningMode.on_expiry.equals(currentWindow.getReasoningMode())) {
				res = doOntologicalAR(currentWindow);
				activitiesForCurrentWindow.addAll(res);
			}
			currentWindow.setActive(false);
			windowToActivitiesMap.put(currentWindow, activitiesForCurrentWindow);
			ontologyManager.clearData();
			currentWindow = new Window(timeManager.getCurrentTime());
			currentWindow.setActive(true);
//			System.err.println(i);
			i++;
//			System.out.println("**********time : " + timeManager.getCurrentTime()+" Start window "+ i+"************");
			//System.out.println("start fenetre "+ i);
		}
		dataManager.close();
	}
	
	public List<Activity> doOntologicalAR(Window w) {
		List<Activity> list;
		list = ontologyManager.callOntology(w);
		if(list.isEmpty()) {
			return list;
		}
		if(list.get(0).getLabel().equals("Nothing")) {
			w.updateDataSet();
			ontologyManager.clearDisabledProperties();
			w.setStartTime(timeManager.getCurrentTime());
			list = ontologyManager.callOntology(w);
		}
		if (list.size() == 1) {
			Activity currentActivity = list.get(0);
			if (currentActivity.isSpecific()) {
				if(!w.attemptShrink(currentActivity, timeManager.getCurrentTime())) {
					w.attemptExpand(currentActivity);
				}
			} else {
				w.attemptExpand(currentActivity);
			}
		} else {
			Activity maxActivity  = getMaxDuration(list);
			if(w.isExpandable()) {
				w.attemptExpand(maxActivity);
			}
		}
		return list;
	}
	
	private Activity getMaxDuration(List<Activity> list) {
		Activity maxActivity = null;
		int m = 0;
		for(Activity a : list) {
			int curDuration = a.getDuration();
			if(!a.isSpecific()) {
				curDuration = getMaxChildrenDuration(a);
			}
			if(m < curDuration) {
				m = curDuration;
				maxActivity = a;
			}
		}
		return maxActivity;
	}
	
	private int getMaxChildrenDuration(Activity activity) {
		Set<OWLClass> children = ontologyManager.getActivitySubClasses(activity);
		int maxDuration = 0;
		ActivityManager activityManager = ActivityManager.getInstance();
		for(OWLClass c : children) {
			if(ontologyManager.isSpecific(c)) {
				String label = c.getIRI().getFragment();
				maxDuration = Math.max(maxDuration, activityManager.getLength(label));
			}
		}
		return maxDuration;
	}

}
