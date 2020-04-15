package com.infostretch.jersy.suite;

import java.io.FileNotFoundException;
import java.io.IOException;

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

public class TicketTagTestCases extends BaseTest {
	@Test(testName = "Verifying newly created Ticket Tag", priority = 0)
	public void createTicketTag() throws FileNotFoundException, IOException, ParseException {
		Reporter.log("Verifying newly created Ticket Tag");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("createTicketTag.json"));
		Response response = target.path(ConfigUtil.getProperty("post.create.tickettag")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token"))
				.post(Entity.entity(jsonObject, MediaType.valueOf("application/vnd.api+json")));
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);

		int statusCode = response.getStatus();

		if (statusCode == 201) {
			String ticketID = responseDocumentContext.read("data.id");
			setTicketTagId(ticketID);
			Assert.assertTrue(true);
		} else {
			Reporter.log("Ticket tag is not created");
		}
		Reporter.log("Ticket tag is created and number is " + getTicketTagId());
		Reporter.log("Ticket tag is created with name as " + responseDocumentContext.read("data.attributes.name"));
		Assert.assertEquals(responseDocumentContext.read("data.attributes.name"), "ticket-tag-name");
	}

	@Test(testName = "Verifying details of a specific ticket tag", priority = 1)
	public void ticketTagDetails() throws ParseException {
		Reporter.log("Verifying details of a specific ticket tag");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.tickettag.details") + getTicketTagId()).request()
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		if (response.getStatus() == 200) {
			Assert.assertEquals(response.getHeaderString("Content-Type"), "application/vnd.api+json");
			Reporter.log("Response header is expected, application/vnd.api+json and actual is "
					+ response.getHeaderString("Content-Type"));
		}
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("data.attributes.name"), "ticket-tag-name");
		Reporter.log("Correct Ticket tag type is found and attribute name is expected, ticket-tag-name and actual is "
				+ responseDocumentContext.read("data.attributes.name"));
	}

	@Test(testName = "Verifying details of a deleted event type", priority = 3)
	public void deleteTicketTag() throws ParseException {
		Reporter.log("Verifying details of a deleted event type");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("delete.tickettag") + getTicketTagId()).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).delete(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("meta.message"), "Object successfully deleted");
		Reporter.log("Ticket tag with Id as: " + getTicketTagId() + " is successfully deleted with message "
				+ responseDocumentContext.read("meta.message"));
	}
}
