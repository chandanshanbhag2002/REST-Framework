package haplous.rest.assertion;

import com.jayway.restassured.response.Response;

import haplous.rest.init.XMLSuite;
import haplous.rest.services.ExtentReport;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;

public class ResponseBodyAssertion {
	public static String ResponseAssert;
	public static Logger logger = Logger.getLogger(ResponseBodyAssertion.class);

	public static void assertResponseBody(String responseBody, Response r) {
		try {
			File file = new File(responseBody);
			JSONParser parser = new JSONParser();
			String fileName = file.getName();
			String filename = null;
			if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
				filename = fileName.substring(fileName.lastIndexOf(".") + 1);
			} else {
				filename = responseBody;
			}

			if (filename.equalsIgnoreCase("json")) {
				logger.info("**====**Reading Response Body From : " + XMLSuite.ScriptPath + XMLSuite.FolderName
						+ File.separator + "Response" + File.separator + responseBody);
				String jsonSchema = RestAPI.readFile(XMLSuite.ScriptPath + XMLSuite.FolderName + File.separator
						+ "Response" + File.separator + responseBody);
				jsonSchema = RestAPI.appendRandomNo(jsonSchema);
				jsonSchema = jsonSchema.replaceAll("ï»¿", "");
				Object obj1 = parser.parse(r.asString());
				Object obj2 = parser.parse(jsonSchema);
				try {
					RestAPI.compare(obj1, obj2);
					logger.info("**====**Response Body Assertion Successfull");
				} catch (Throwable e) {
					ResponseAssert = "Response body assertion failed";
					logger.info("Response body assertion failed");
					logger.info(
							"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					ExtentReport.failTest();
				}
			} else {
				logger.info("**====**Reading Response Body From excel");
				responseBody = RestAPI.appendRandomNo(responseBody);
				Object obj1 = parser.parse(r.asString());
				Object obj2 = parser.parse(responseBody);
				try {
					RestAPI.compare(obj1, obj2);
					logger.info("**====**Response Body Assertion Successfull");
				} catch (Throwable e) {
					logger.info("**====**Response Body Assertion Failure");
					logger.info(
							"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					
					ResponseAssert = "Response body assertion failed";
					ExtentReport.failTest();
				}
			}
		} catch (Exception e) {
			XMLSuite.genericError="Exception caused atResponseBodyAssertion()<\br><b>"+e.toString()+"<\b>";
			logger.info("Exception caused at atResponseBodyAssertion()"+e.toString());
			logger.info(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			
			ExtentReport.failTest();
		}
	}
}
