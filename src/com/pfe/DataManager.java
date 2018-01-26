package com.pfe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pfe.entities.Window;

public class DataManager {
	public final String FILE = "resources/data3";
	BufferedReader br;
	String line;
	private static DataManager instance = null;

	
	private DataManager() {	
		try {
			File file = new File(FILE);
			this.br = new BufferedReader(new FileReader(file));
			this.line = br.readLine();
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
	
	public boolean readLine(int currentTime, Window w) {
		boolean ret = false;
		try {
			List<String> newData = new ArrayList<>();
			while (line != null && Integer.parseInt(line.split("@")[0]) <= currentTime) {
				if(w.getEffectiveStartTime() == -1) {
					w.setEffectiveStartTime(currentTime);
				}
				w.addSensor(line);
				newData.add(line);
				line = br.readLine();
				ret = true;
			}
			w.setNewDataSet(newData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
