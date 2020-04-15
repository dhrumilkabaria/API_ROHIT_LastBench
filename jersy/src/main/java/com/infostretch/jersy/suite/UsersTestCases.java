package com.infostretch.jersy.suite;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.infostretch.jersy.common.BaseTest;
import com.infostretch.jersy.utility.ConfigUtil;
import com.infostretch.jersy.utility.JSONFileReader;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class UsersTestCases extends BaseTest {

	@Test(testName = "Verifying response status code for 'GET' request for list of users", priority = 0)
	public void getAllUsers() throws ParseException {
		Reporter.log("Verifying response status code for 'GET' request");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.all.users")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		List<String> totalUsersList = documentContext.read("data[*].id");
		Reporter.log("Total number of users is: " + totalUsersList.size());
	}

	@Test(testName = "Verifying newly added user", priority = 1)
	public void verifyingNewUser() throws IOException, ParseException {
		Reporter.log("Verifying newly added user");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("newUser.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Response response = target.path(ConfigUtil.getProperty("post.new.user")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token"))
				.post(Entity.entity(jsonObject, MediaType.valueOf("application/vnd.api+json")));
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 201,
				"Actual response status code: " + statusCode + " match with expected response status code: " + 201);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("data.attributes.email"),
				documentContext.read("data.attributes.email"));
		String userID = responseDocumentContext.read("data.id");
		setUserID(userID);
		Reporter.log("New user is created with user id as: " + userID);
	}

	@Test(testName = "Verifying deleted user", priority = 3)
	public void deleteUser() throws ParseException {
		Reporter.log("Verifying deleted user");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("delete.user") + getUserID()).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).delete(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("meta.message"), "Object successfully deleted");
		Reporter.log("User with id as: " + getUserID() + " is successfully deleted with message "
				+ responseDocumentContext.read("meta.message"));
	}

	@Test(testName = "Verifying logout from the application", priority = 4)
	public void logOut() throws ParseException {
		Reporter.log("Verifying logout from the application");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("post.logout")).request().post(null);
		Assert.assertEquals(response.getStatus(), 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		if (responseDocumentContext.read("success")) {
			Reporter.log("User logout successfully");
		} else {
			Reporter.log("User not logged out");
		}
	}
}
