package com.infostretch.jersy.suite;

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

public class TaxTestCases extends BaseTest {

	@Test(testName = "Verifying response status code for 'GET' request for list of all taxes", priority = 4)
	public void getAllTaxes() {
		Reporter.log("Verifying response status code for 'GET' request for list of all taxes");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.all.taxes")).request()
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		Assert.assertEquals(response.getStatus(), 200, "Actual response status code: " + response.getStatus());
	}

	@Test(testName = "Verifying newly added tax", priority = 0)
	public void verifyingNewAddedTax() throws IOException, ParseException {
		Reporter.log("Verifying newly added tax");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("createNewTax.json"));
		Response response = target.path(ConfigUtil.getProperty("post.new.tax")).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token"))
				.post(Entity.entity(jsonObject, MediaType.valueOf("application/vnd.api+json")));
		Assert.assertEquals(response.getStatus(), 201, "Actual response status code: " + response.getStatus()
				+ " match with expected response status code: " + 201);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		String taxId = responseDocumentContext.read("data.type.id");
		setTaxId(taxId);
		Reporter.log("New tax is created with id as: " + taxId);
	}

	@Test(testName = "Verifying response status code for 'GET' request for single tax", priority = 2)
	public void getSingleTax() {
		Reporter.log("Verifying response status code for 'GET' request for single tax");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("get.single.tax")).request()
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).get(Response.class);
		Assert.assertEquals(response.getStatus(), 200, "Actual response status code: " + response.getStatus());
	}

	@Test(testName = "Verifying deleted tax", priority = 1)
	public void deleteTax() throws ParseException {
		Reporter.log("Verifying deleted tax");
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(ConfigUtil.getProperty("base.url"));
		Response response = target.path(ConfigUtil.getProperty("delete.tax") + getEventId()).request()
				.header("Content-Type", "application/json")
				.header("Authorization", "JWT " + ConfigUtil.getProperty("access_token")).delete(Response.class);
		int statusCode = response.getStatus();
		Assert.assertEquals(statusCode, 200);
		JSONObject responseJsonObject = (JSONObject) new JSONParser().parse(response.readEntity(String.class));
		DocumentContext responseDocumentContext = JsonPath.parse(responseJsonObject);
		Assert.assertEquals(responseDocumentContext.read("meta.message"), "Object successfully deleted");
		Reporter.log("Tax with Tax-Id as: " + getTaxId() + " is successfully deleted with message "
				+ responseDocumentContext.read("meta.message"));
	}

}
