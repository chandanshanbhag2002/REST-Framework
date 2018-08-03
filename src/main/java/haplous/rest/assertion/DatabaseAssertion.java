package haplous.rest.assertion;

import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.response.Response;

import haplous.rest.init.XMLSuite;
import haplous.rest.services.ExtentReport;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import static haplous.rest.init.XMLSuite.UpdatedRequestBody;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseAssertion {
    public static String DatabaseAssert;
    public static Logger logger = Logger.getLogger(DatabaseAssertion.class);

    public static void assertDatabase(String assertDatabaseJson, Response r) {
        try {
            String sqlquery;
            String sqlqueryreturntype;
            String requestbodyasertpath;
            String responsebodtasertpath;
            String source = null;
            String destination;
            String expectedData;
            JSONObject respObj = null, reqObj = null;
            String updatedAssertDatabaseJson = RestAPI.appendRandomNo(assertDatabaseJson);
            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject) parser.parse(updatedAssertDatabaseJson);
            sqlquery = (String) jobj.get("sqlQuery");
            sqlqueryreturntype = (String) jobj.get("sqlQueryReturnType");
            requestbodyasertpath = (String) jobj.get("requestBodyAssert");
            responsebodtasertpath = (String) jobj.get("responseBodyAssert");
            expectedData = (String) jobj.get("expectedData");
            logger.info("**====**Query : " + sqlquery + "returnType : " + sqlqueryreturntype);
           
            Statement stmt = XMLSuite.con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlquery);
            switch (sqlqueryreturntype) {
                case "int":
                    while (rs.next()) {
                        source = String.valueOf(rs.getInt(1));
                    }
                    ;
                    break;
                case "string":
                    while (rs.next()) {
                        source = rs.getString(1);
                    }
                    ;
                    break;
                case "date":
                    while (rs.next()) {
                        source = String.valueOf(rs.getDate(1));
                    }
                    ;
                    break;
                default:
                    logger.info("**====*Invalid DataType Specified in DataBase Assertion field, Hence DataBase Assertion Failed");
                	logger.info(
    						"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    DatabaseAssert = "Invalid DataType Specified in DataBase Assetion field";
                    ExtentReport.failTest();
            }
            logger.info("**====**Source value : " + source);
           if(!UpdatedRequestBody.equals("null") || !UpdatedRequestBody.isEmpty()){
        	   
            if (UpdatedRequestBody.startsWith("[")) {
                reqObj = (JSONObject) parser
                        .parse(String.valueOf(UpdatedRequestBody.substring(1, UpdatedRequestBody.length() - 1)));
            } else {
                reqObj = (JSONObject) parser.parse(String.valueOf(UpdatedRequestBody));
            }
           }
            if (r.contentType().equalsIgnoreCase("")) {
                logger.info("**====** Response body is blank, cannot fetch destination value from response body");
            }else{
                respObj = (JSONObject) parser.parse(String.valueOf(r.asString()));
            }
            // get destination to compare with

            if (!requestbodyasertpath.equalsIgnoreCase("null")) {
                destination = JsonPath.read(reqObj.toString(), requestbodyasertpath);
                logger.info("**====**Fetching destination value from requestbody");
            } else if (!responsebodtasertpath.equalsIgnoreCase("null")) {
                Object temp = JsonPath.read(respObj.toString(), responsebodtasertpath);
                destination = String.valueOf(temp);
                logger.info("**====**Fetching destination value from response");
            } else {
                destination = expectedData;
            }
            logger.info("**====**destination value : " + destination);
            if (source != null && destination != null) {
                try {
                    Assert.assertEquals(source, destination, "**====** database assertion failed");
                    logger.info("**====** database assertion is successful");
                } catch (Throwable e) {
                    logger.info("**====** database assertion failed");
                	logger.info(
    						"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    DatabaseAssert = "Mismatch in Actual and Expeted data :" + "</br><b>Actual data : </b> = "
                            + destination + "</br><b>Expected data : </b>" + source;
                    ExtentReport.failTest();
                }
            } else {
                logger.info("**====** database assertion failed");
                DatabaseAssert = "Database assertion failure,expected Data not available in DB";
                Assert.assertEquals("", destination,
                        "Asserting source  :  " + source + " with destination : " + destination + "failed");
                ExtentReport.failTest();
            }

        } catch (Exception e) {
            logger.info("**====** Exception while asserting with DataBase" + "\n" + e.toString());
        	logger.info(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            DatabaseAssert = "Exception while asserting with DataBase,Database assertion failure,expected Data not available in DB";
            ExtentReport.failTest();
        }
    }
}
