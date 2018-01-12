package com.pfe.entities;

public class Activity {
	
	private boolean specific;
	private boolean asserted;
	private int startTime;
	private int duration;
	private String label;
	
	public Activity(String label) {
		this.label = label;
		this.startTime = 0;
		this.duration = 50;
		this.specific = false;
		this.asserted = false;
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
