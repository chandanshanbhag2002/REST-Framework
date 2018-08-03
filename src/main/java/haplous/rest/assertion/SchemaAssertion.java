package haplous.rest.assertion;

import com.jayway.restassured.response.Response;

import haplous.rest.init.XMLSuite;
import haplous.rest.services.ExtentReport;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;

public class SchemaAssertion {
	public static String SchemaAssert;
	public static Logger logger = Logger.getLogger(SchemaAssertion.class);

	public static void assertRespSchema(String responseSchemaFile, Response r) throws IOException {
		try {
			File file = new File(responseSchemaFile);
			String fileName = file.getName();
			String filename = null;
			if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
				filename = fileName.substring(fileName.lastIndexOf(".") + 1);
			} else {
				filename = responseSchemaFile;
			}

			if (filename.equalsIgnoreCase("json")) {
				logger.info("**====**Reading Response Schema From : " + XMLSuite.SchemaHome + responseSchemaFile);
				String jsonSchema = RestAPI.readFile(XMLSuite.SchemaHome + responseSchemaFile);
				jsonSchema = jsonSchema.replaceAll("ï»¿", "");
				try {
					assertThat(r.asString(), matchesJsonSchema(jsonSchema));
					logger.info("**====**Schema Assertion Successfull");
				} catch (Throwable e) {
					SchemaAssert = "Schema assertion failed";
					logger.info("Schema assertion failed");
					logger.info(
							"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					
					ExtentReport.failTest();
				}

			} else {
				logger.info("**====**Reading Response Schema From excel");
				try {
					responseSchemaFile = responseSchemaFile.replaceAll("ï»¿", "");
					assertThat(r.asString(), matchesJsonSchema(responseSchemaFile));
					logger.info("**====**Schema Assertion Successfull");
				} catch (Throwable e) {
					SchemaAssert = "Schema assertion failed";
					logger.info("Schema assertion failed");
					logger.info(
							"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					Assert.fail("Schema assertion failed");
					ExtentReport.failTest();
				}
			}
		} catch (Exception e) {
			XMLSuite.genericError="Exception caused at assertRespSchema()<\br><b>"+e.toString()+"<\b>";
			logger.info("Exception caused at assertRespSchema()"+e.toString());
			logger.info(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			ExtentReport.failTest();
		}
	}
}
