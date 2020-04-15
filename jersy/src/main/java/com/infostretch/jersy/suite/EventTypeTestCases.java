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

public class EventTypeTestCases extends BaseTest {
	@Test(testName = "Verifying response status code for 'GET' request for list of events", priority = 0)
	public void getAllEvents() throws ParseException {
		Reporter.log("Verifying response status code for 'GET' request");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.all.eventtype")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		List<String> totalEventsType = documentContext.read("data[*].id");
		Reporter.log("Total number of events type is: " + totalEventsType.size());
	}

	@Test(testName = "Verifying newly added event type", priority = 1)
	public void verifyingAddedNewEventType() throws IOException, ParseException {
		Reporter.log("Verifying newly added event");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("newEventType.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Response response = target.path(ConfigUtil.getProperty("post.new.eventtype")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token"))
				.post(Entity.entity(jsonObject, MediaType.valueOf("application/vnd.api+json")));
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 201,
				"Actual response status code: " + statusCode + " match with expected response status code: " + 201);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("data.attributes.name"),
				documentContext.read("data.attributes.name"));
		String eventId = responseDocumentContext.read("data.id");
		setEventId(eventId);
		Reporter.log("New event is created with id as: " + eventId + " and event type as: "
				+ responseDocumentContext.read("data.type"));
	}

	@Test(testName = "Verifying deleted event type", priority = 3)
	public void deleteEvent() throws ParseException {
		Reporter.log("Verifying deleted event type");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("delete.eventtype") + getEventId()).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).delete(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("meta.message"), "Object successfully deleted");
		Reporter.log("Event Type ID as: " + getEventId() + " is successfully deleted with message "
				+ responseDocumentContext.read("meta.message"));
	}

	@Test(testName = "Verifying a single event type details", priority = 2)
	public void eventTypesDetails() throws ParseException {
		Reporter.log("Verifying a single event type details");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.eventtype.details") + getEventId()).request()
				.header("Accept", "application/vnd.api+json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("data.attributes.name"), "Jersy Sample Event1234");
		Reporter.log("Specific event type name is:  " + responseDocumentContext.read("data.attributes.name"));
	}
}
