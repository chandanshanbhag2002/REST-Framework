package haplous.rest.requestmethod;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import java.io.File;
import static com.jayway.restassured.RestAssured.given;
import static haplous.rest.init.XMLSuite.ScriptPath;

public class Upload {
	public final Logger logger = Logger.getLogger(Upload.class);
	public Response getResponse(String endpoint, String UploadFileName, String content_type) throws Exception {
        File absoluteFilePath = new File(ScriptPath + "IUP" + File.separator + UploadFileName);
        Response r = null;
        JSONParser parser = new JSONParser();
        String Fil = absoluteFilePath.getName();
        logger.info("**====** Absolute file path to be imported : "+absoluteFilePath);
        logger.info("**====** file to be imported : " + Fil);
        try {
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder = RestAPI.setHeader(content_type);
            RequestSpecification requestSpec = builder.build();
            Thread.sleep(10000);
            r = given(requestSpec).multiPart("content", absoluteFilePath).multiPart("filename", Fil).when().post(endpoint);
            logger.info("**====**Response Code in GenericPost Class : " + r.getStatusCode());
            logger.info("**====**Response Body in GenericPost Class : " + r.asString());
			StatusCodeAssertion.assertStatusCode(r);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return r;
	}
}
