package window;

import java.util.ArrayList;
import java.util.List;

public class Window {
	
	/**
	 * start time .
	 */
	private long startTime;
	
	/**
	 * end time .
	 */
	private double endTime;
	
	/**
	 * initial length of time window .
	 */
	private long lengthInit;
	
	/**
	 * length of time window .
	 */
	private double length;
	
	/**
	 * sensor data set .
	 */
	private SensorDataSet set;
	
	/**
	 * vector of activity labels .
	 */
	private List<String> activityLabels = new ArrayList<String>();
	
	/** 
	 * reasoning start mode .
	 */
	private ReasoningMode startMode;
	
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
	private boolean active = false;
	
	private boolean shrinkable;
	
	private boolean shrinkableAndExpandable;
	
	
	/**
	 * constructor .
	 */
	public Window(long start, double end, double length, ReasoningMode rm, 
			double windowFactor, double slidingFactor, double changeFactor) {
		this.startTime = start ;
		this.endTime = end ;
//		this.lengthInit = length ; 
		this.length = length ;
		this.startMode = rm ;
		this.windowFactor = windowFactor ;
		this.slidingFactor = slidingFactor ;
		this.changeFactor = changeFactor ;
	}

	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public double getLengthInit() {
		return lengthInit;
	}
	
	public void setLengthInit(long lengthInit) {
		this.lengthInit = lengthInit;
	}


	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public double getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public SensorDataSet getSet() {
		return set;
	}

	public void setSet(SensorDataSet set) {
		this.set = set;
	}

	public List<String> getActivityLabels() {
		return activityLabels;
	}

	public void setActivityLabels(List<String> activityLabels) {
		this.activityLabels = activityLabels;
	}

	public ReasoningMode getStartMode() {
		return startMode;
	}

	public void setStartMode(ReasoningMode startMode) {
		this.startMode = startMode;
	}

	public double getWindowFactor() {
		return windowFactor;
	}

	public void setWindowFactor(double windowFactor) {
		this.windowFactor = windowFactor;
	}

	public double getSlidingFactor() {
		return slidingFactor;
	}

	public void setSlidingFactor(double slidingFactor) {
		this.slidingFactor = slidingFactor;
	}

	public double getChangeFactor() {
		return changeFactor;
	}

	public void setChangeFactor(double changeFactor) {
		this.changeFactor = changeFactor;
	}
	
	public void calcEndTime() {
		endTime = startTime + length ;
	}
	
	public void calcLength() {
		length = windowFactor * lengthInit ;
	}


	public boolean isShrinkable() {
		return shrinkable;
	}


	public void setShrinkable(boolean shrinkable) {
		this.shrinkable = shrinkable;
	}


	public boolean isShrinkableAndExpandable() {
		return shrinkableAndExpandable;
	}


	public void setShrinkableAndExpandable(boolean shrinkableAndExpandable) {
		this.shrinkableAndExpandable = shrinkableAndExpandable;
	}
	
	
	public double pendingTime() {
		return endTime - System.currentTimeMillis();
	}
	
	public void expand(double time) {
		this.endTime = this.endTime + time;
		calcLength();
	}
	
	public void shrink() {
		this.endTime = System.currentTimeMillis();
	}
}
