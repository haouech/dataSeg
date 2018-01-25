package com.pfe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		Logger.init();
		int i=0;
		Window initWindow = new Window(timeManager.getCurrentTime());
		Window currentWindow = initWindow ;	
		Map<Window,Set<Activity>> windowToActivitiesMap = new HashMap<>();
		currentWindow.setActive(true);
		while (timeManager.isStillRunning()) 
		{
			Set<Activity> activitiesForCurrentWindow = new HashSet<>();
			List<Activity> res = null;
			while (timeManager.isStillRunning() && currentWindow.getEndTime() > timeManager.getCurrentTime())
			{
				//Lecture du dataSet 
				dataManager.readLine(timeManager.getCurrentTime(), currentWindow);
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
			if(m < a.getDuration()) {
				m = a.getDuration();
				maxActivity = a;
			}
		}
		return maxActivity;
	}

}
