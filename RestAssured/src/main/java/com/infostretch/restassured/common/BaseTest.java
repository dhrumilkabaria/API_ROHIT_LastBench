package com.infostretch.restassured.common;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.infostretch.restassured.utility.ConfigUtil;
import com.infostretch.restassured.utility.JSONFileReader;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseTest {
	private String accessToken;
	private String userID;
	private String eventId;
	private String taxId;
	private Header authorizationHeader;
	private String ticketTagId;

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

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

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public void setAuthorizationHeader(String token) {
		authorizationHeader = new Header("Authorization", "JWT " + token);
	}

	@BeforeSuite
	public void initProperties() {
		ConfigUtil.initProperties();
	}

	@BeforeTest()
	public void authenticateUser() throws IOException, ParseException {
		RestAssured.baseURI = ConfigUtil.getProperty("base.url");
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json");
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("authentication.json"));
		request.body(jsonObject.toJSONString());
		Response response = request.post(ConfigUtil.getProperty("post.token"));
		String accessToken = response.jsonPath().getString("access_token");
		setAuthorizationHeader(accessToken);
	}
}
