package com.pfe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ActivityManager {
	
	private static ActivityManager instance = null;
	
	private Map<String, Integer> activityToLength;
	
	public ActivityManager() {
		this.activityToLength = readLine("resources/activities.txt");
	}
	
	public static ActivityManager getInstance() {
		if(instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}
	
	private Map<String, Integer> readLine(String file){
		String line;
		BufferedReader bufferReader;
		Map<String, Integer> activityToLength = new HashMap<>();
		try {
			bufferReader = new BufferedReader(new FileReader(file));
			line = bufferReader.readLine();
	        while(line != null) {
	        	String[] parts = line.split(":");
	        	String label = parts[0];
	        	String Activitylength = parts[1];
	        	int length = Integer.parseInt(Activitylength);
	        	activityToLength.put(label, length);
	        	line = bufferReader.readLine();
	        }
			
	        bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return activityToLength;
	}
	
	public Map<String, Integer> getActivitiesLength() {
		return activityToLength;
	}
	
	public int getLength(String label) {
		Integer res = activityToLength.get(label);
		if(res == null) {
			return 0;
		}
		return res ;
	}
}
