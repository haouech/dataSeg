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
		int i=1;
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
					System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
					for(Activity a : res) {
						System.out.println(a.getLabel());
					}
				}
				else if(ReasoningMode.at_intervals.equals(currentWindow.getReasoningMode()) 
						&& timeManager.isInterval())
				{
					res = doOntologicalAR(currentWindow);
//					activitiesForCurrentWindow.addAll(res);
					System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
					for(Activity a : res) {
						System.out.println(a.getLabel());
					}
				}
				timeManager.advanceTime();
				// TODO: Handle data input
			} // End of window
			activitiesForCurrentWindow.addAll(res);
			if(ReasoningMode.on_expiry.equals(currentWindow.getReasoningMode())) 
			{
				res = doOntologicalAR(currentWindow);
				activitiesForCurrentWindow.addAll(res);
				System.out.println("**********time : " + timeManager.getCurrentTime()+"************");
				for(Activity a : res) {
					System.out.println(a.getLabel());
				}
			}
//			discardPreviousSensorActivation();
			currentWindow.setActive(false);
			windowToActivitiesMap.put(currentWindow, activitiesForCurrentWindow);
			ontologyManager.clearData();
			currentWindow = new Window(timeManager.getCurrentTime());
			currentWindow.setActive(true);
			System.out.println("fentre "+ i);
			i++;
		}
		dataManager.close();
	}
	
	public List<Activity> doOntologicalAR(Window w) {
		List<Activity> list;
		list = ontologyManager.callOntology(w);
		if(list.size()==0) {
			return list;
		}
		if (list.size() == 1) 
		{
			Activity currentActivity = list.get(0);
			if (currentActivity.isSpecific()) 
			{
				// if not shrinkable add identified activities to final list here
				System.out.println("One possible activity identified");
				if (!w.isShrinkable())
				{
					if(currentActivity.isAsserted()) 
					{
						System.out.println("Activity identified: " + currentActivity.getLabel());
						Window.finalList.add("Activity identified: "+currentActivity.getLabel());
					}
					if(currentActivity.isExhausted(timeManager.getCurrentTime())) 
					{
						System.out.println("Time Exhausted for: " + currentActivity.getLabel());
						Window.finalList.add("Time Exhausted for: "+currentActivity.getLabel());
					}
				}
				if(!w.attemptShrink(currentActivity, timeManager.getCurrentTime())) {
					System.out.println("more sensor data needed");
					w.attemptExpand(currentActivity);
				}
			} 
			else 
			{
				System.out.println("Activity not specific");
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
			if(w.isExpandable()) {
				w.attemptExpand(maxActivity);
			}
		}
		return list;
	}

}
