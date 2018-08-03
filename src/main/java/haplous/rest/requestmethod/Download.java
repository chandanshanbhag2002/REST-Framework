package haplous.rest.requestmethod;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import static com.jayway.restassured.RestAssured.given;
import static haplous.rest.init.XMLSuite.ScriptPath;

import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.InputStream;

public class Download {
    public final Logger logger = Logger.getLogger(Download.class);

    public Response getResponse(String endpoint, String UploadFileName, String content_type) throws Exception {
        UploadFileName = RestAPI.appendRandomNo(UploadFileName);
        JSONParser parser = new JSONParser();
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder = RestAPI.setHeader("download");
        RequestSpecification requestSpec = builder.build();
        Thread.sleep(5000);
        Response r = given().spec(requestSpec).when().get(endpoint);
        logger.info("**====**Response Code in GenericPost Class : " + r.getStatusCode());
        logger.info("**====**Response Body in GenericPost Class : " + r.asString());

        StatusCodeAssertion.assertStatusCode(r);
        try{
            String saveDir= ScriptPath + "IUP" + File.separator + UploadFileName;
            // opens an output stream to save into file
            InputStream inputStream = r.asInputStream();
            RestAPI.downloadFile(inputStream, saveDir);
        } catch (Exception e) {
           logger.info("**====**Error while downloading a file");
        }

        return r;
    }
}
