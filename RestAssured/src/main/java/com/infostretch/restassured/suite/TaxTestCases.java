package com.infostretch.restassured.suite;

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
import io.restassured.specification.RequestSpecification;

public class TaxTestCases extends BaseTest {

	@Test(testName = "Verifying response status code for 'GET' request for list of all taxes", priority = 4)
	public void getAllTaxes() {
		Reporter.log("Verifying response status code for 'GET' request for list of all taxes");
		Response response = RestAssured.given().header(getAuthorizationHeader()).when()
				.get(ConfigUtil.getProperty("get.all.taxes")).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200, "Actual response status code: " + response.getStatusCode());
	}

	@Test(testName = "Verifying newly added tax", priority = 0)
	public void verifyingNewAddedTax() throws IOException, ParseException {
		Reporter.log("Verifying newly added tax");
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(JSONFileReader.getJSONBody("createNewTax.json"));
		Headers headers = Headers.headers(getAuthorizationHeader(),
				new Header("Content-Type", "application/vnd.api+json"));
		Response response = httpRequest.headers(headers).body(jsonObject).when()
				.post(ConfigUtil.getProperty("post.new.tax")).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 201, "Actual response status code: " + response.getStatusCode()
				+ " match with expected response status code: " + 201);
		String taxId = response.path("data.type.id");
		setTaxId(taxId);
		Reporter.log("New tax is created with id as: " + taxId);
	}

	@Test(testName = "Verifying updated details for a tax", priority = 5)
	public void updateTaxDetails() throws ParseException, IOException {
		Reporter.log("Verifying updated details for a tax");
		JSONObject jsonObject = (JSONObject) new JSONParser()
				.parse(JSONFileReader.getJSONBody("updateTaxDetails.json"));
		DocumentContext documentContext = JsonPath.parse(jsonObject);
		Headers headers = new Headers(new Header("Content-Type", "application/vnd.api+json"), getAuthorizationHeader());
		Response response = RestAssured.given().headers(headers).body(jsonObject).when()
				.patch(ConfigUtil.getProperty("patch.update.tax") + documentContext.read("data.id")).then().extract()
				.response();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("data.attributes.city"), documentContext.read("data.attributes.city"));
		Reporter.log("City of TAX with TAX ID as :" + documentContext.read("data.id") + " updated as: "
				+ documentContext.read("data.attributes.city"));
	}

	@Test(testName = "Verifying response status code for 'GET' request for single tax", priority = 2)
	public void getSingleTax() {
		Reporter.log("Verifying response status code for 'GET' request for single tax");
		Response response = RestAssured.given().header(getAuthorizationHeader()).when()
				.get(ConfigUtil.getProperty("get.single.tax")).then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200, "Actual response status code: " + response.getStatusCode());
	}

	@Test(testName = "Verifying deleted tax", priority = 1)
	public void deleteTax() {
		Reporter.log("Verifying deleted tax");
		Headers headers = new Headers(new Header("Accept", "application/vnd.api+json"), getAuthorizationHeader());
		Response response = RestAssured.given().headers(headers).when().delete(ConfigUtil.getProperty("delete.tax"))
				.then().extract().response();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.path("meta.message"), "Object successfully deleted");
		Reporter.log("Tax with Tax-Id as: " + getTaxId() + " is successfully deleted with message "
				+ response.path("meta.message"));
	}

}
