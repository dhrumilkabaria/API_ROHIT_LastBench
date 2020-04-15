package com.infostretch.json.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.testng.Reporter;

public class JSONUtility {
	static String filePath = System.getProperty("user.dir") + "\\testData";
	static String fileName = "sample-data.json";

	public static FileReader getFileReader() {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath + "\\" + fileName);
		} catch (FileNotFoundException fileNotFoundException) {
			Reporter.log("File " + fileName + " not present in " + filePath);
		}
		return fileReader;
	}
}
