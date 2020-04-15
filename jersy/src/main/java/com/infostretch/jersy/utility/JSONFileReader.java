package com.infostretch.jersy.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.testng.Reporter;

public class JSONFileReader {
	static String filePath = System.getProperty("user.dir") + "\\testData";

	public static FileReader getJSONBody(String fileName) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath + "\\" + fileName);
		} catch (FileNotFoundException fileNotFoundException) {
			Reporter.log("File " + fileName + " not present in " + filePath);
		}
		return fileReader;
	}
}
