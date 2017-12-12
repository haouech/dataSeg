package window;

import java.util.ArrayList;
import java.util.List;

public class Ontology {
	
	private long neededTime;
	private List<SensorDataSet> sensorSet = new ArrayList<>(); 

	public long getNeededTime() {
		return neededTime;
	}

	public void setNeededTime(long neededTime) {
		this.neededTime = neededTime;
	}

	public List<SensorDataSet> getSensorSet() {
		return sensorSet;
	}

	public void setSensorSet(List<SensorDataSet> sensorSet) {
		this.sensorSet = sensorSet;
	}
	
}