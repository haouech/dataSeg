package window;


public class Activity {
	
	private boolean specific;
	private boolean asserted;
	private long startTime = System.currentTimeMillis();
	private int duration;

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
		return (startTime + duration < System.currentTimeMillis());
	}
	
	public long getTimeToComplete() {
		return startTime + duration - System.currentTimeMillis();
	}

}
