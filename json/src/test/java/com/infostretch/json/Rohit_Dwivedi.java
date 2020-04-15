package com.infostretch.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.infostretch.json.utility.JSONUtility;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Rohit_Dwivedi {

	@Test(testName = "Displaying all users")
	public void getAllUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());

		String jsonPath = "$.[*].name";
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read(jsonPath);
		Iterator<String> iterator = list.iterator();
		int count = 0;
		Reporter.log("All users-");
		while (iterator.hasNext()) {
			count++;
			Reporter.log(count + "." + iterator.next());
		}
	}

	@Test(testName = "Displaying all female users")
	public void getAllFemaleUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read("[?(@.gender=='female')].name");
		Iterator<String> iterator = list.iterator();
		int count = 0;
		Reporter.log("All female users-");
		while (iterator.hasNext()) {
			count++;
			Reporter.log(count + "." + iterator.next());
		}
	}

	@Test(testName = "Displaying first two users")
	public void getFirstTwoUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read("$.[0,1].name");
		Iterator<String> iterator = list.iterator();
		int count = 0;
		Reporter.log("First two users-");
		while (iterator.hasNext()) {
			count++;
			Reporter.log(count + "." + iterator.next());
		}
	}

	@Test(testName = "Displaying last two users")
	public void getLastTwoUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read("$.[-2:].name");
		Iterator<String> iterator = list.iterator();
		int count = 0;
		Reporter.log("Last two users-");
		while (iterator.hasNext()) {
			count++;
			Reporter.log(count + "." + iterator.next());
		}
	}

	@Test(testName = "Displaying total number of friends for first user")
	public void getTotalFriendsOfFirstUser() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read("$.[0].friends");
		Reporter.log("Total number of first users are- " + list.size());
	}

	@Test(testName = "Displaying all users whose age is between 24 and 36")
	public void getUsersWithAgeConstraint() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		List<String> list = documentContext.read("[?(@.age>24 && @.age<36)].name");
		Iterator<String> iterator = list.iterator();
		int count = 0;
		Reporter.log("All users whose age is between 24 and 36-");
		while (iterator.hasNext()) {
			count++;
			Reporter.log(count + "." + iterator.next());
		}
	}

	@Test(testName = "Displaying balance for Male users")
	public void getBalanceOfMaleUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		Filter filter = Filter.filter(Criteria.where("gender").eq("male"));
		List<Map<String, Object>> list = documentContext.read("$.[*][?]", filter);
		Iterator<Map<String, Object>> iterator = list.iterator();
		int count = 0;
		Reporter.log("Displaying balance for Male users-");
		while (iterator.hasNext()) {
			count++;
			Map<String, Object> map = iterator.next();
			Reporter.log(count + ". User: " + map.get("name") + " || Balance: " + map.get("balance") + "<br>");
		}
	}

	@Test(testName = "Displaying users live in California")
	public void getCaliforniaUsers() throws ParseException {
		Object object = new JSONParser(0).parse(JSONUtility.getFileReader());
		DocumentContext documentContext = JsonPath.parse(object);
		Filter filter = Filter.filter(Criteria.where("address").contains("California"));
		List<Map<String, Object>> list = documentContext.read("$.[*][?]", filter);
		Iterator<Map<String, Object>> iterator = list.iterator();
		int count = 0;
		Reporter.log("Displaying users live in California-");
		while (iterator.hasNext()) {
			count++;
			Map<String, Object> map = iterator.next();
			Reporter.log(count + ". User: " + map.get("name") + " || Address: " + map.get("address") + "<br>");
		}
	}

}
