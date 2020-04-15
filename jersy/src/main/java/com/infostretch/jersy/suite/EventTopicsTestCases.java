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

public class EventTopicsTestCases extends BaseTest {
	@Test(testName = "Verifying response status code for 'GET' request for list of event topics", priority = 0)
	public void getAllEventTopics() throws ParseException {
		Reporter.log("Verifying response status code for 'GET' request");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.event.topics")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		List<String> totalEventsTopics = documentContext.read("data[*].id");
		Reporter.log("Total number of events topics is: " + totalEventsTopics.size());
	}

	@Test(testName = "Verifying newly added event topics", priority = 1)
	public void verifyingAddedNewEventTopic() throws IOException, ParseException {
		Reporter.log("Verifying newly added event topic");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		JSONObject jsonObject = (JSONObject) new JSONParser()
				.parse(JSONFileReader.getJSONBody("createNewEventTopic.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Response response = target.path(ConfigUtil.getProperty("post.event.topics")).request()
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
		String eventTopicId = responseDocumentContext.read("data.id");
		setEventTopicId(eventTopicId);
		Reporter.log("New event topic is created with id as: " + eventTopicId + " and event topic as: "
				+ responseDocumentContext.read("data.type"));
	}

	@Test(testName = "Verifying deleted event topic", priority = 3)
	public void deleteEventTopic() throws ParseException {
		Reporter.log("Verifying deleted event topic");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("delete.event.topics") + getEventTopicId()).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).delete(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("meta.message"), "Object successfully deleted");
		Reporter.log("Event Topic ID as: " + getEventTopicId() + " is successfully deleted with message "
				+ responseDocumentContext.read("meta.message"));
	}

	@Test(testName = "Verifying a single event topic details", priority = 2)
	public void eventTopicDetails() throws ParseException {
		Reporter.log("Verifying a single event topic details");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.event.topic.details") + getEventTopicId()).request()
				.header("Accept", "application/vnd.api+json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("data.attributes.name"), "Travel & Outdoor");
		Reporter.log("Specific event topic name is:  " + responseDocumentContext.read("data.attributes.name"));
	}
}
