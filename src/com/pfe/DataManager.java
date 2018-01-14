package com.pfe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.pfe.entities.Window;

public class DataManager {
	public final String FILE = "resources/input.txt";
	BufferedReader br;
	File file;
	String line;
	private static DataManager instance = null;

	
	private DataManager() {	
		try {
			this.file = new File(FILE);
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
	
	public void readLine(int currentTime, Window w) {
		try {
			while (line != null && Integer.parseInt(line.split("@")[0]) == currentTime) {
				w.getSet().add(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
