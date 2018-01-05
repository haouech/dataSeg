package window;


public class Activity {
	
	private boolean specific;
	private boolean asserted;
	private double startTime;
	private double duration;
	private String label;
	private Activity parent;
	private Subclass subClass;
	
	public Activity(String label) {
		this.label = label;
		this.startTime = 0.0;
		this.duration = 0.0;
		this.subClass = null;
		this.parent = null;
		this.specific = false;
		this.asserted = false;
	}

	public Activity(String label, double start, double duration, Subclass subClass, Activity parent) {
		this.label = label;
		this.startTime = start;
		this.duration = duration;
		this.subClass = subClass;
		this.parent = parent;
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

	public boolean isExhausted() {
		return false; //(startTime + duration < System.currentTimeMillis());
	}
	
	public double getTimeToComplete() {
		return startTime + duration - System.currentTimeMillis();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Subclass getSubClass() {
		return subClass;
	}

	public void setSubClass(Subclass subClass) {
		this.subClass = subClass;
	}

	public Activity getParent() {
		return parent;
	}

	public void setParent(Activity parent) {
		this.parent = parent;
	}
	@Override
	public String toString() {
		return label;
	}
}