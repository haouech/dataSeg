package com.pfe.entities;

import com.pfe.ActivityManager;
import com.pfe.TimeManager;

public class Activity {
	
	private boolean specific;
	private boolean asserted;
	private int startTime;
	private int duration;
	private int assertionTime;
	private String label;
	
	public Activity(String label) {
		this.label = label;
		this.startTime = 0;
		this.duration = ActivityManager.getInstance().getLength(label);
		this.assertionTime = -1;
		this.specific = false;
		this.asserted = false;
	}

	public int getStartTime(int time) {
		return this.startTime;
	}
	
	public void setStartTime(int time) {
		this.startTime = time;
	}

	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public boolean isSpecific() {
		return specific;
	}

	public void setSpecific(boolean specific) {
		this.specific = specific;
	}

	public boolean isAsserted() {
		return asserted;
	}

	public void setAsserted(boolean asserted) {
		this.asserted = asserted;
	}
	
	public int getAssertionTime() {
		return assertionTime;
	}
	
	public int getEndTime() {
		return startTime + duration;
	}

	public boolean isExhausted(int currentTime) {
		return getEndTime() < currentTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
