package com.pfe;

public class TimeManager {
	
	private int currentTime = 0;
	private int endTime;
	private boolean interval = false;
	private int intervalTime;
	private final int INTERVAL_LENGTH = 10;
	private final int END_TIME = 1000;
	
	public TimeManager() {
		this.currentTime = 0;
		this.endTime = END_TIME;
		this.interval = false;
		this.intervalTime = 0;
	}
	
	public TimeManager(int time) {
		this();
		this.currentTime = time;
		this.endTime = currentTime + END_TIME;
	}
	
	public TimeManager(int time, int endTime) {
		this(time);
		if(endTime >= this.currentTime) {
			this.endTime = endTime;
		}
	}
	
	public boolean advanceTime() {
		if(interval) {
			interval = false;
		}
		currentTime++;
		intervalTime++;
		if(intervalTime >= INTERVAL_LENGTH) {
			interval  = true;
			intervalTime = 0;
		}
		if(currentTime > endTime) {
			return false;
		}
		return true;
	}
	
	public boolean advanceTime(int time) throws Exception {
		if(time < currentTime) {
			throw new Exception("Input time is less than current time");
		}
		if(interval) {
			interval = false;
		}
		currentTime = time;
		intervalTime += (time - currentTime);
		if(intervalTime >= INTERVAL_LENGTH) {
			interval  = true;
			intervalTime = 0;
		}
		if(!isStillRunning()) {
			return false;
		}
		return true;
	}
	
	public boolean isStillRunning() {
		return currentTime < endTime;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	
	public int getEndTime() {
		return endTime;
	}
	
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	public boolean isInterval() {
		return interval;
	}
	
}
