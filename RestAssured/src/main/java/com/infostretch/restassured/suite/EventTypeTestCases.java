package com.infostretch.restassured.suite;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.infostretch.restassured.common.BaseTest;
import com.infostretch.restassured.utility.ConfigUtil;
import com.infostretch.restassured.utility.JSONFileReader;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;

public class EventTypeTestCases extends BaseTest {

	@Test(testName = "Getting list of Event Types", priority = 0)
	public void getListEventTypes() {
		Reporter.log("Getting list of Event Types");
		Response response = RestAssured.given().header("Accept", "application/vnd.api+json")
				.header(getAuthorizationHeader()).when().get(ConfigUtil.getProperty("get.all.eventtype")).then()
				.statusCode(200).extract().response();
		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			Assert.assertTrue(true);
		}
		Reporter.log("Specific event type name is " + response.path("data[2].attributes.name"));
		Assert.assertEquals(response.path("data[2].attributes.name"), "Camp, Treat & Retreat");
	}

	@Test(testName = "Verifying newly added event type", priority = 1)
	public void addingNewEventType() throws FileNotFoundException, IOException, ParseException {
		Reporter.log("Verifying newly added event type");
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("newEventType.JSON"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Headers headers = new Headers(new Header("Content-Type", "application/vnd.api+json"), getAuthorizationHeader());
		Response response = RestAssured.given().headers(headers).body(jsonObject).when()
				.post(ConfigUtil.getProperty("post.new.eventtype")).then().extract().response();
		String eventId = response.path("data.id");
		int statusCode = response.getStatusCode();
		if (statusCode == 201) {
			setEventId(eventId);
			Assert.assertTrue(true);
		} else {
			Reporter.log("Event is not created");
		}
		Reporter.log("Newly created event type id is: " + eventId);
		Reporter.log("Event is created with name as " + response.path("data.attributes.name"));
		Assert.assertEquals(response.path("data.attributes.name"), documentContext.read("data.attributes.name"));
	}

	@Test(testName = "Verifying a single event type details", priority = 2)
	public void eventTypesDetails() {
		Reporter.log("Verifying a single event type details");
		Response response = RestAssured.given().header("Accept", "application/vnd.api+json")
				.header(getAuthorizationHeader()).when()
				.get(ConfigUtil.getProperty("get.eventtype.details") + getEventId());
		if (response.getStatusCode() == 200) {
			Assert.assertEquals(response.getHeader("Content-Type"), "application/vnd.api+json");
			Reporter.log("Response header is expected, application/vnd.api+json and actual is "
					+ response.getHeader("Content-Type"));
		}
		Assert.assertEquals(response.path("data.attributes.name"), "Sample Event1234");
		Reporter.log("Specific event type name is:  " + response.path("data.attributes.name"));
	}

	@Test(testName = "Verifying details of an updated event type", priority = 3)
	public void updateEventType() throws FileNotFoundException, IOException, ParseException {
		Reporter.log("Verifying details of an updated event type");
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("updateEventType.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Response response = RestAssured.given().header("Content-Type", "application/vnd.api+json")
				.header(getAuthorizationHeader()).body(jsonObject).when()
				.patch(ConfigUtil.getProperty("patch.update.eventtype") + documentContext.read("data.id")).then()
				.extract().response();
		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(response.path("data.attributes.name"), "Camp, Treat & Retreat");
		Reporter.log("Event type is updated with new name as: " + response.path("data.attributes.name"));
	}

	@Test(testName = "Verifying details of a deleted event type", priority = 4)
	public void deleteEventType() {
		Reporter.log("Verifying details of a deleted event type");
		Response response = RestAssured.given().header("Accept", "application/vnd.api+json")
				.header(getAuthorizationHeader()).when()
				.delete(ConfigUtil.getProperty("delete.eventtype") + getEventId()).then().statusCode(200).extract()
				.response();
		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(response.path("meta.message"), "Object successfully deleted");
		Reporter.log("Event type is successfully deleted with actual message as " + response.path("meta.message"));
	}
}
