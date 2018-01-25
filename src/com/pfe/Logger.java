package com.pfe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Logger {
	static private String FILENAME = "resources/output.log";
	static private PrintStream writer;
	
	static void init() {
		try {
			FileOutputStream outStream = new FileOutputStream(new File(FILENAME));
			writer = new PrintStream(outStream);
		} catch (FileNotFoundException e) {
			System.err.println("File: " + FILENAME + " not found");
		}
	}
	
	public static void print(String data) {
		writer.print(data);
	}
	
	public static void println(String data) {
		writer.println(data);
	}
	
	public static void write(int time, String data) {
		String t = String.format("%6d", time);
		writer.println(t + "> " + data);
	}
}
