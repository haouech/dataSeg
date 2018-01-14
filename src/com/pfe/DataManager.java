package com.pfe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataManager {
	
	private static DataManager instance = null;
	private BufferedReader bufferReader;
	
	private DataManager() {
		
		try {
			bufferReader = new BufferedReader(new FileReader("input.txt"));
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DataManager getInstance() {
		if(instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	public void readLine() {
		
	}
}
