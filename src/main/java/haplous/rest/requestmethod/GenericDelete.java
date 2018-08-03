package haplous.rest.requestmethod;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class GenericDelete {
	public final Logger logger = Logger.getLogger(GenericDelete.class);

	public Response getResponse(String endpoint, String body, String content_type) throws Exception {
		JSONParser parser = new JSONParser();
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder = RestAPI.setHeader(content_type);
		if (body != null) {
			if (body.startsWith("[")) {

				builder.setBody(body);
			} else {
				JSONObject bodyJsonObj = (JSONObject) parser.parse(body);
				builder.setBody(bodyJsonObj.toJSONString());
			}
		} else {
			logger.info("**====**Delete request contains header parameter only");
		}
		RequestSpecification requestSpec = builder.build();
		Response r = given().spec(requestSpec).when().delete(endpoint);
		logger.info("**====**Response Code in GenericDelete Class : " + r.getStatusCode());
		logger.info("**====**Response Body in GenericDelete Class : " + r.asString());

		StatusCodeAssertion.assertStatusCode(r);
		return r;
	}
}
