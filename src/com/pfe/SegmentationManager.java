package com.pfe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pfe.entities.Window;

import com.pfe.entities.Activity;
import window.ReasoningMode;
//import window.Window;

public class SegmentationManager {

	private OntologyManager ontologyManager;
	private TimeManager timeManager;
	
	public SegmentationManager() {
		ontologyManager = OntologyManager.getOntologyManager();
		timeManager = new TimeManager();
	}
	
	public void recognizeADL(int start, int length) {

		Window initWindow = new Window(timeManager.getCurrentTime(), length);
		Window currentWindow = initWindow ;		
		Map<Window,Set<Activity>> windowToActivitiesMap = new HashMap<>();
		
		currentWindow.setActive(true);
		while (timeManager.isStillRunning()) 
		{
			Set<Activity> activitiesForCurrentWindow = new HashSet<>();
			while (currentWindow.getEndTime() > timeManager.getCurrentTime())
			{
				if(ReasoningMode.on_sensor.equals(currentWindow.getReasoningMode()) 
						|| ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode())) {
					List<Activity> res = doOntologicalAR(currentWindow);
					activitiesForCurrentWindow.addAll(res);
				}
				else if(ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode()) 
						&& timeManager.isInterval())
				{
					List<Activity> res = doOntologicalAR(currentWindow);
					activitiesForCurrentWindow.addAll(res);
				}
				timeManager.advanceTime();
			}
			if(ReasoningMode.on_expiry.equals(currentWindow.getReasoningMode())) 
			{
				List<Activity> res = doOntologicalAR(currentWindow);
				activitiesForCurrentWindow.addAll(res);
			}
//			discardPreviousSensorActivation();
			windowToActivitiesMap.put(currentWindow, activitiesForCurrentWindow);
			//If overlapping true 
			if(currentWindow.getSlidingFactor() == 1) {
				currentWindow.setActive(false);
				currentWindow = new Window(timeManager.getCurrentTime(), length);
				ontologyManager = OntologyManager.getOntologyManager();
			}
		}
	}
	
	public List<Activity> doOntologicalAR(Window w) {
		List<Activity> list;
		list = ontologyManager.callOntology(w.getSet());
		if(list.size()==0) {
			return list;
		}
		if (list.size() == 1) 
		{
			Activity currentActivity = list.get(0);
			if (currentActivity.isSpecific()) 
			{
				System.out.println("One possible activity identified");
				if(w.isShrinkable()) {
					if(!w.attemptShrink(currentActivity, timeManager.getCurrentTime())) {
						System.out.println("more sensor data needed");
						w.attemptExpand(currentActivity);
					}
				}
			} 
			else 
			{
				System.out.println("more sensor data needed");
				w.attemptExpand(currentActivity);
			}
		} 
		else 
		{
			//Obtain generic activity labels parent activity?????????????????
//			Activity parentActivity = list.get(0).getParent();
//			if(w.isShrinkableAndExpandable()) {
//				attemptExpansion(w, parentActivity);
//			}
		}
		return list;
	}

}
