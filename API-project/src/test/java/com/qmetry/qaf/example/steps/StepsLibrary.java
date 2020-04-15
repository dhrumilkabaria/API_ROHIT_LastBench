package com.qmetry.qaf.example.steps;

import java.util.List;

import org.testng.Reporter;

import com.jayway.jsonpath.JsonPath;
import com.qmetry.qaf.automation.step.QAFTestStep;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.qmetry.qaf.automation.ws.rest.RestWSTestCase;

public class StepsLibrary extends RestWSTestCase {

  @QAFTestStep(description = "getting number of items for jsonPath {0}")
  public static void gettingNumberOfItemsInList(String jsonPath) {
    List<Object> itemsList = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
        jsonPath);
    Reporter.log("Total number of items at JosnPath: " + jsonPath + ": " + itemsList.size());
    int i = 1;
    for (Object object : itemsList) {
      Reporter.log(i + ". " + object.toString());
      i++;
    }
  }
}
