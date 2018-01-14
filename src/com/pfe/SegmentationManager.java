package com.pfe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pfe.entities.Window;

import com.pfe.entities.Activity;
import window.ReasoningMode;

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

		Window initWindow = new Window(timeManager.getCurrentTime());
		Window currentWindow = initWindow ;		
		Map<Window,Set<Activity>> windowToActivitiesMap = new HashMap<>();
		
		currentWindow.setActive(true);
		while (timeManager.isStillRunning()) 
		{
			Set<Activity> activitiesForCurrentWindow = new HashSet<>();
			while (timeManager.isStillRunning() && currentWindow.getEndTime() > timeManager.getCurrentTime())
			{
				//Lecture du dataSet 
				dataManager.readLine(timeManager.getCurrentTime(), currentWindow);
				if(ReasoningMode.on_sensor.equals(currentWindow.getReasoningMode()) 
						|| ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode())) {
					List<Activity> res = doOntologicalAR(currentWindow);
					activitiesForCurrentWindow.addAll(res);
					System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
					for(Activity a : res) {
						System.out.println(a.getLabel());
					}
				}
				else if(ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode()) 
						&& timeManager.isInterval())
				{
					List<Activity> res = doOntologicalAR(currentWindow);
					activitiesForCurrentWindow.addAll(res);
					System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
					for(Activity a : res) {
						System.out.println(a.getLabel());
					}
				}
				timeManager.advanceTime();
				// TODO: Handle data input
			}
			if(ReasoningMode.on_expiry.equals(currentWindow.getReasoningMode())) 
			{
				List<Activity> res = doOntologicalAR(currentWindow);
				activitiesForCurrentWindow.addAll(res);
				System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
				for(Activity a : res) {
					System.out.println(a.getLabel());
				}
			}
//			discardPreviousSensorActivation();
			windowToActivitiesMap.put(currentWindow, activitiesForCurrentWindow);
			currentWindow.setActive(false);
			ontologyManager.clearData();
			currentWindow = new Window(timeManager.getCurrentTime());
			currentWindow.setActive(true);
		}
		dataManager.close();
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
			int m = 0;
			Activity maxActivity = null;
			//Obtain generic activity labels parent activity?????????????????
			for(Activity a : list) {
				if(m < a.getDuration()) {
					m = a.getDuration();
					maxActivity = a;
				}
			}
//			Activity parentActivity = list.get(0).getParent();
			if(w.isShrinkable() && w.isExpandable()) {
				w.attemptExpand(maxActivity);
			}
		}
		return list;
	}

}
