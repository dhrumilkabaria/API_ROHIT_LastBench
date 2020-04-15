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
import io.restassured.response.Response;

public class TicketTagTestCases extends BaseTest {
	@Test(testName = "Verifying newly created Ticket Tag", priority = 0)
	public void createTicketTag() throws FileNotFoundException, IOException, ParseException {
		Reporter.log("Verifying newly created Ticket Tag");
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("createTicketTag.json"));
		Response response = RestAssured.given().header("Content-Type", "application/vnd.api+json")
				.header(getAuthorizationHeader()).body(jsonObject).when()
				.post(ConfigUtil.getProperty("post.create.tickettag")).then().extract().response();
		String ticketID = response.path("data.id");
		int statusCode = response.getStatusCode();
		if (statusCode == 201) {
			setTicketTagId(ticketID);
			Assert.assertTrue(true);
		} else {
			Reporter.log("Ticket tag is not created");
		}
		Reporter.log("Ticket tag is created and number is " + ticketID);
		Reporter.log("Ticket tag is created with name as " + response.path("data.attributes.name"));
		Assert.assertEquals(response.path("data.attributes.name"), "ticket-tag-name");
	}

	@Test(testName = "Verifying details of a specific ticket tag", priority = 1)
	public void ticketTagDetails() {
		Reporter.log("Verifying details of a specific ticket tag");
		Response response = RestAssured.given().header("Accept", "application/vnd.api+json")
				.header(getAuthorizationHeader()).when().get(ConfigUtil.getProperty("get.tickettag.details"));
		if (response.getStatusCode() == 200) {
			Assert.assertEquals(response.getHeader("Content-Type"), "application/vnd.api+json");
			Reporter.log("Response header is expected, application/vnd.api+json and actual is "
					+ response.getHeader("Content-Type"));
		}
		Assert.assertEquals(response.path("data.attributes.name"), "ticket-tag-name");
		Reporter.log("Correct Ticket tag type is found and attribute name is expected, ticket-tag-name and actual is "
				+ response.path("data.attributes.name"));
	}

	@Test(testName = "Verifying deatils of an updated tciket tag", priority = 2)
	public void updateTicketTag() throws FileNotFoundException, IOException, ParseException {
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("updateTicketTag.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Response response = RestAssured.given().header("Content-Type", "application/vnd.api+json")
				.header("Authorization", "JWT " + getAccessToken()).body(jsonObject).when()
				.patch(ConfigUtil.getProperty("patch.update.tickettag") + documentContext.read("data.id")).then()
				.extract().response();
		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(response.path("data.attributes.name"), documentContext.read("data.attributes.name"));
		Reporter.log("Ticket tag type is updated with new name as: " + response.path("data.attributes.name"));
	}

	@Test(testName = "Verifying details of a deleted event type", priority = 3)
	public void deleteTicketTag() {
		Reporter.log("Verifying details of a deleted event type");
		Response response = RestAssured.given().header("Accept", "application/vnd.api+json")
				.header("Authorization", "JWT " + getAccessToken()).when()
				.delete(ConfigUtil.getProperty("delete.tickettag") + getTicketTagId()).then().statusCode(200).extract()
				.response();
		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			Assert.assertTrue(true);
		}
		Assert.assertEquals(response.path("meta.message"), "Object successfully deleted");
		Reporter.log("Ticket tag is successfully deleted with actual message as " + response.path("meta.message"));
	}
}
