package com.infostretch.restassured.suite;

import java.io.IOException;
import java.util.List;

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
import io.restassured.specification.RequestSpecification;

public class UsersTestCases extends BaseTest {

	@Test(testName = "Verifying response status code for 'GET' request for list of users", priority = 0)
	public void getAllUsers() {
		Reporter.log("Verifying response status code for 'GET' request");
		Response response = RestAssured.given().header(getAuthorizationHeader()).when()
				.get(ConfigUtil.getProperty("get.all.users")).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200, "Actual response status code: " + response.getStatusCode()
				+ " match with expected response status code: " + 200);
		List<String> totalUsersList = response.jsonPath().get("data.id");
		Reporter.log("Total number of users is: " + totalUsersList.size());
	}

	@Test(testName = "Verifying newly added user", priority = 1)
	public void verifyingNewUser() throws IOException, ParseException {
		Reporter.log("Verifying newly added user");
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("newUser.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Headers headers = Headers.headers(getAuthorizationHeader(),
				new Header("Content-Type", "application/vnd.api+json"));
		Response response = httpRequest.headers(headers).body(jsonObject).when()
				.post(ConfigUtil.getProperty("post.new.user")).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 201, "Actual response status code: " + response.getStatusCode()
				+ " match with expected response status code: " + 201);
		Assert.assertEquals(response.getBody().jsonPath().get("data.attributes.email"),
				documentContext.read("data.attributes.email"));
		String userID = response.path("data.id");
		setUserID(userID);
		Reporter.log("New user is created with user id as: " + userID);
	}

	@Test(testName = "Verifying updated details for a user", priority = 2)
	public void updateUserDetails() throws ParseException, IOException {
		Reporter.log("Verifying updated details for a user");
		JSONObject jsonObject = (JSONObject) new JSONParser()
				.parse(JSONFileReader.getJSONBody("updateUserDetails.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Headers headers = new Headers(new Header("Content-Type", "application/vnd.api+json"), getAuthorizationHeader());
		Response response = RestAssured.given().headers(headers).body(jsonObject).when()
				.patch(ConfigUtil.getProperty("patch.update.users") + documentContext.read("data.id")).then().extract()
				.response();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("data.attributes.first-name"),
				documentContext.read("data.attributes.first-name"));
		Reporter.log("First Name of user: " + response.path("data.attributes.first-name") + " updated as: "
				+ documentContext.read("data.attributes.first-name"));
	}

	@Test(testName = "Verifying deleted user", priority = 3)
	public void deleteUser() {
		Reporter.log("Verifying deleted user");
		Headers headers = new Headers(new Header("Accept", "application/vnd.api+json"), getAuthorizationHeader());
		Response response = RestAssured.given().headers(headers).when()
				.delete(ConfigUtil.getProperty("delete.user") + getUserID()).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("meta.message"), "Object successfully deleted");
		Reporter.log("User is successfully deleted with message " + response.path("meta.message"));
	}

	@Test(testName = "Verifying logout from the application", priority = 4)
	public void logOut() {
		Reporter.log("logout from the application");
		Response response = RestAssured.given().when().post(ConfigUtil.getProperty("post.logout"));
		if (response.statusCode() == 200) {
			Reporter.log("User logouts from the application.");
		}
	}

}
