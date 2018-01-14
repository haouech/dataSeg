package com.pfe.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pfe.ActivityManager;
import com.pfe.OntologyManager;
import com.pfe.entities.Activity;
import window.ReasoningMode;
import org.semanticweb.owlapi.model.OWLClass;

public class Window {
	

	/**
	 * start time .
	 */
	private int startTime;
	
	/**
	 * end time .
	 */
	private int endTime;
	
	/**
	 * length of time window .
	 */
	private int length;
	
	/**
	 * sensor data set .
	 */
	private List<String> sensorDataSet;
	
	/**
	 * vector of activity labels .
	 */
	private List<String> activityLabels;
	
	/** 
	 * reasoning start mode .
	 */
	private ReasoningMode reasoningMode;
	
	/**
	 * time window factor .
	 */
	private double windowFactor;
	
	/**
	 * sliding factor .
	 */
	private double slidingFactor;
	
	/**
	 * change factor .
	 */
	private double changeFactor;
	
	/**
	 * active .
	 */
	private boolean active;

	/**
	 * shrinkable .
	 */
	private boolean shrinkable;

	/**
	 * expandable .
	 */
	private boolean expandable;
	
	private final int DEFAULT_LENGTH = 3;
	private final double DEFAULT_WINDOW_FACTOR = 1;
	private final double DEFAULT_SLIDING_FACTOR = 1;
	private final double DEFAULT_CHANGE_FACTOR = 1;
	private OntologyManager ontologyManager;
	
	public Window() {
		this.startTime = 0; 
		this.length = DEFAULT_LENGTH;
		this.endTime = startTime + length;
		this.reasoningMode = ReasoningMode.on_sensor;
		this.windowFactor = DEFAULT_WINDOW_FACTOR;
		this.slidingFactor = DEFAULT_SLIDING_FACTOR;
		this.changeFactor = DEFAULT_CHANGE_FACTOR;
		this.sensorDataSet = new ArrayList<String>();
		this.activityLabels = new ArrayList<>();
		this.shrinkable = true;
		this.expandable = true;
		this.active = false;
		ontologyManager = OntologyManager.getInstance();
	}
	public Window(int start) {
		this();
		this.startTime = start;
		this.endTime = this.startTime + length;
	}

	
	public boolean attemptShrink(Activity activity, int curTime) {
		if(!shrinkable) {
			return false;
		}
		if(activity.isAsserted() || activity.isExhausted(curTime)) {
			shrink(curTime);
			System.out.println("Activity identified: " + activity.getLabel());
			return true;
		}
		if(activity.getEndTime() >= endTime) {
			return attemptExpand(activity);
		}
		return false;
	}
	
	public boolean attemptExpand(Activity activity) {
		if(!expandable) {
			return false;
		}
		if(activity.isSpecific() && !activity.isAsserted() && 
				activity.getEndTime() > endTime){
			expand(activity.getEndTime());
			return true;
		}
		if(sensorDataSet.size()>0) { // TODO: check condition
			int maxSubClassesDuration = getMaximumDurationFromSubclasses(activity);
			if(maxSubClassesDuration > endTime) {
				expand(this.startTime + maxSubClassesDuration);
				return true;
			}
		}
		return false;
	}
	
	private int getMaximumDurationFromSubclasses(Activity activity) {
		Set<OWLClass> subClassesSet = ontologyManager.getActivitySubClasses(activity);
		Map<String, Integer> activitiesToLength = ActivityManager.getInstance().getActivitiesLength();
		
		int maxDuration = 0;
		for(OWLClass c : subClassesSet) {
			String label = c.getIRI().getFragment();
			if(activitiesToLength.get(label) != null) {
				maxDuration = Math.max(maxDuration, activitiesToLength.get(label));
			}
		}
		return maxDuration;
	}

	
	public double pendingTime(int time) {
		if(time > endTime) {
			return 0;
		}
		return endTime - time;
	}
	
	public void expand(int time) {
		this.endTime = time;
		updateLength();
	}
	
	public void shrink(int currentTime) {
		this.endTime = currentTime;
		updateLength();
	}
	
	private void updateLength() { 
		this.length = this.endTime - this.startTime;
	}
	
	private void updateEndTime() {
		this.endTime = this.startTime + this.length;
	}
	
	/*
	 * Setters and Getters
	 */
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
		updateLength();
	}

	public double getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		updateEndTime();
	}
	
	public boolean isShrinkable() {
		return shrinkable;
	}

	public void setShrinkable(boolean shrinkable) {
		this.shrinkable = shrinkable;
	}
	
	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}
	
	public ReasoningMode getReasoningMode() {
		return reasoningMode;
	}
	
	public void setReasoningMode(ReasoningMode resMode) {
		this.reasoningMode = resMode;
	}
	
	public double getSlidingFactor() {
		return slidingFactor;
	}

	public List<String> getSet() {
		return sensorDataSet;
	}

	public void setSet(List<String> set) {
		this.sensorDataSet = set;
	}
	
	public void addSensor(String sensor) {
		sensorDataSet.add(sensor);
	}

}
