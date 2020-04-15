package com.infostretch.jersy.common;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.infostretch.jersy.utility.ConfigUtil;
import com.infostretch.jersy.utility.JSONFileReader;

public class BaseTest {
	private String accessToken;
	private String userID;
	private String eventId;
	private String taxId;
	private String eventTopicId;

	public String getEventTopicId() {
		return eventTopicId;
	}

	public void setEventTopicId(String eventTopicId) {
		this.eventTopicId = eventTopicId;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	private String ticketTagId;

	public String getTicketTagId() {
		return ticketTagId;
	}

	public void setTicketTagId(String ticketTagId) {
		this.ticketTagId = ticketTagId;
	}

	public String getEventId() {
		return eventId;

	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getAccessToken() {
		this.accessToken = ConfigUtil.getProperty("access_token");
		return accessToken;
	}

	@BeforeSuite
	public void initProperties() {
		ConfigUtil.initProperties();
	}

	@BeforeTest()
	public void authenticateUser() throws IOException, ParseException {
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("authentication.json"));
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
			Response response = target.path(ConfigUtil.getProperty("post.token")).request()
					.header("Content-Type", "application/json").post(Entity.json(json));
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
			String accessToken = jsonObject.get("access_token").toString();
			ConfigUtil.setProperty("access_token", accessToken);
		} catch (Exception e) {
			Reporter.log("Error occured in setAccesstokenMethod: " + e.getMessage());
		}

	}
}
