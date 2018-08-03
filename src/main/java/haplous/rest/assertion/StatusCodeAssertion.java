package haplous.rest.assertion;

import com.jayway.restassured.response.Response;

import haplous.rest.init.XMLSuite;
import haplous.rest.services.ExcelUtil;
import haplous.rest.services.ExtentReport;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;

import static haplous.rest.init.XMLSuite.TestSteps;
import static haplous.rest.init.XMLSuite.cases;
import static haplous.rest.init.XMLSuite.tcid;

import java.net.HttpURLConnection;

public class StatusCodeAssertion {
	public static final Logger logger = Logger.getLogger(StatusCodeAssertion.class);
	public static Response response = null;

	public static void assertStatusCode(Response r) throws Exception {
		
		int rowNum;
		String responseSchemaFile, responseBody, queryAssertion;
		JSONParser parser = new JSONParser();
		response = r;
		if (r.getStatusCode() != HttpURLConnection.HTTP_OK && r.getStatusCode() != HttpURLConnection.HTTP_CREATED
				&& r.getStatusCode() != HttpURLConnection.HTTP_NO_CONTENT) {
			if (parser.parse(r.asString()) instanceof JSONObject || parser.parse(r.asString()) instanceof JSONArray) {
				logger.info("**====**MissMatch in the response code");

				ExtentReport.failTest();
				Assert.fail(r.getStatusLine() + "\n" + r.asString() + "\n");
				// throw new Exception(r.getStatusLine() + "\n" + r.asString() +
				// "\n");
			} else {
				ExtentReport.failTest();
				Assert.fail(r.getStatusLine() + "\n Please Contact system administrator");
				// throw new Exception(r.getStatusLine()+"\n Please Contact
				// system administrator" );
			}
		} else {

			rowNum = ExcelUtil.getRowNum(TestSteps, tcid, cases);
			responseSchemaFile = ExcelUtil.getRandcellData(TestSteps, rowNum, cases, "SchemaAssertion");
			responseBody = ExcelUtil.getRandcellData(TestSteps, rowNum, cases, "ResponseBodyAssertion");
			queryAssertion = ExcelUtil.getRandcellData(TestSteps, rowNum, cases, "DataBaseAsertion");

			// Schema Assertion
			if (!responseSchemaFile.isEmpty() && !responseSchemaFile.equalsIgnoreCase("null")) {
				SchemaAssertion.assertRespSchema(responseSchemaFile, r);
			} else {
				logger.info("**====**Skipping Schema Assertion");
			}

			// ResponseBodyAssertion
			if (!responseBody.isEmpty() && !responseBody.equalsIgnoreCase("null")) {
				ResponseBodyAssertion.assertResponseBody(responseBody, r);
			} else {
				logger.info("**====**Skipping ResponseBody Assertion");
			}

			// Database Assertion
			if (!queryAssertion.isEmpty() && !queryAssertion.equalsIgnoreCase("null")) {
				DatabaseAssertion.assertDatabase(queryAssertion, r);
			} else {
				logger.info("**====**Skipping Database Assertion");
			}
		
			if(XMLSuite.persistState){
				XMLSuite.depmap.put((XMLSuite.testName+"_"+XMLSuite.tcid).toLowerCase(),r);
			}
			
			ExtentReport.passTest();
		}

	}
}
