package haplous.rest.requestmethod;

import com.jayway.restassured.response.Response;

import haplous.rest.assertion.DatabaseAssertion;
import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.init.XMLSuite;
import haplous.rest.services.ExcelUtil;
import haplous.rest.services.RestAPI;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static haplous.rest.init.XMLSuite.TestSteps;
import static haplous.rest.init.XMLSuite.cases;
import static haplous.rest.init.XMLSuite.tcid;

import java.sql.ResultSet;
import java.sql.Statement;

public class GenericFind {
    public final Logger logger = Logger.getLogger(GenericFind.class);

    public Response getResponse(String endpoint, String body, String content_type) throws Exception {
        int rowNum,rowCount = 0;
        String sqlquery,queryAssertion,source = null;
        Response r = null;
        rowNum = ExcelUtil.getRowNum(TestSteps, tcid, cases);
        queryAssertion = ExcelUtil.getRandcellData(TestSteps, rowNum, cases, "DataBaseAsertion");
        String updatedAssertDatabaseJson = RestAPI.appendRandomNo(queryAssertion);
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject) parser.parse(updatedAssertDatabaseJson);
        sqlquery = (String) jobj.get("sqlQuery");
        Statement stmt = XMLSuite.con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlquery);
        while (rs.next()) {
            rowCount++;
        };
        if(rowCount <= 0){
            logger.info("**====**Calling generic Post to create object");
            GenericPost p = new GenericPost();
            p.getResponse(endpoint,body,content_type);
        }else{
            logger.info("**====** Object already exists, Hence skipping object creation");
        }
        return r;
    }
}
