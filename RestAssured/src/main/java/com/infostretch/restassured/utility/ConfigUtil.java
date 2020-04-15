package com.infostretch.restassured.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.testng.Reporter;

public class ConfigUtil {
	static HashMap<String, String> properties;

	public static void initProperties() {
		Properties property = new Properties();
		properties = new HashMap<String, String>();
		try {
			File file = new File(System.getProperty("user.dir") + "\\application.properties");
			property.load(new FileInputStream(file));
			Set<Entry<Object, Object>> entrySet = property.entrySet();
			Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Map.Entry<Object, Object> entry = iterator.next();
				properties.put((String) entry.getKey(), (String) entry.getValue());
			}
		} catch (Exception e) {
			Reporter.log("Error in reading properties file: " + e.getMessage());
		}
	}

	public static String getProperty(String key) {
		return properties.get(key);
	}

	public static void setProperty(String key, String value) {
		properties.put(key, value);
	}

}
