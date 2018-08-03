package haplous.rest.requestmethod;

import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.services.ExtentReport;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;

public class GenericSort {
	public static String SortingAssert;
	public final Logger logger = Logger.getLogger(GenericSort.class);

	public void getResponse(String endpoint, String queryParam, String content_type) throws Exception {
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder = RestAPI.setHeader(content_type);
		RequestSpecification requestSpec = builder.build();
		Response r = given().spec(requestSpec).when().get(endpoint);
		logger.info("**====**Response Code in GenericSort Class : " + r.getStatusCode());
		logger.info("**====**Response Body in GenericSort Class : " + r.asString());
		StatusCodeAssertion.assertStatusCode(r);
		JSONParser jsonParser = new JSONParser();
		JSONObject Actual = (JSONObject) jsonParser.parse(r.asString());

		JSONObject sortBycondition = (JSONObject) jsonParser.parse(queryParam);
		String sortBy = JsonPath.read(sortBycondition.toString(), "$.sortBy");
		logger.info("Sort By : " + sortBy);
		String sortType = JsonPath.read(sortBycondition.toString(), "$.sortType");
		logger.info("Sort Type : " + sortType);
		String objectPath = JsonPath.read(sortBycondition.toString(), "$.objectPath");
		logger.info("Object Path : " + objectPath);

		List<Object> jsonObjArr = JsonPath.read(Actual.toString(), objectPath);
		logger.info("**====**Sorted array list: " + jsonObjArr);
		String StrArr1, StrArr2;
		Integer IntArr1, IntArr2, comp;
		if (sortType.equalsIgnoreCase("asc")) {
			logger.info("**====** Sorting in ascending order");
			for (int j = 0; j <= jsonObjArr.size() - 2; j++) {
				int k = j + 1;

				if (jsonObjArr.get(j) instanceof Integer) {
					IntArr1 = (Integer) jsonObjArr.get(j);
					IntArr2 = (Integer) jsonObjArr.get(k);
					comp = IntArr1.compareTo(IntArr2);

					try {
						Assert.assertTrue((comp <= 0), IntArr1 + " is before " + IntArr2 + "Sorting failed");

					} catch (Throwable e) {
						logger.info("**====** Sorting is ascending order failed");
						SortingAssert = "Sorting in ascending order failed " + "</br><b>Object1 : </b> = " + IntArr1
								+ "</br><b>is greater than Object2 : </b>" + IntArr2;
						ExtentReport.failTest();
					}
				} else {
					StrArr1 = (String) jsonObjArr.get(j);
					StrArr2 = (String) jsonObjArr.get(k);
					comp = StrArr1.compareTo(StrArr2);

					try {
						Assert.assertTrue((comp <= 0), StrArr1 + " is before " + StrArr2 + "Sorting failed");

					} catch (Throwable e) {
						logger.info("**====** Sorting is ascending order failed");
						SortingAssert = "Sorting in ascending order failed " + "</br><b>Object1 : </b> = " + StrArr1
								+ "</br><b>is greater than Object2 : </b>" + StrArr2;
						ExtentReport.failTest();
					}
				}

			}
			logger.info("**====** Sorting in ascending is successful");
		} else {

			logger.info("**====** Sorting in descending order");
			for (int j = 0; j <= jsonObjArr.size() - 2; j++) {
				int k = j + 1;
				if (jsonObjArr.get(j) instanceof Integer) {
					IntArr1 = (Integer) jsonObjArr.get(j);
					IntArr2 = (Integer) jsonObjArr.get(k);
					comp = IntArr1.compareTo(IntArr2);

					try {
						Assert.assertTrue((comp >= 0), IntArr1 + " is before " + IntArr2 + "Sorting failed");

					} catch (Throwable e) {
						logger.info("**====** Sorting is descending order failed");
						SortingAssert = "Sorting in descending order failed " + "</br><b>Object1 : </b> = " + IntArr1
								+ "</br><b>is lesser than Object2 : </b>" + IntArr2;
						ExtentReport.failTest();
					}
				} else {
					StrArr1 = (String) jsonObjArr.get(j);
					StrArr2 = (String) jsonObjArr.get(k);
					comp = StrArr1.compareTo(StrArr2);

					try {
						Assert.assertTrue((comp >= 0), StrArr1 + " is before " + StrArr2 + "Sorting failed");

					} catch (Throwable e) {
						logger.info("**====** Sorting is descending order failed");
						SortingAssert = "Sorting in descending order failed " + "</br><b>Object1 : </b> = " + StrArr1
								+ "</br><b>is lesser than Object2 : </b>" + StrArr2;
						ExtentReport.failTest();
					}
				}

			}
			logger.info("**====** Sorting in descending is successful");
		}
	}
}
