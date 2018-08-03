package haplous.rest.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.parser.JSONParser;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import TestRun.RunSuite;
import haplous.rest.services.*;

public class XMLSuite {


    public static final Logger logger = Logger.getLogger(XMLSuite.class);

    public static Xls_Reader cases;
    public static String userName, URL, dBMachineName, sid, dBUserName, dBPassword, password, UploadFileName, HomeDir,
            MethodName, ScriptPath, endPoint, operation, TestSteps, FolderName, JsonHome, restRequestBody="null",
            UpdatedEndpoint, SchemaHome, loginType, accessToken, dateformat, timeformat, fileName, jSessionId,
            csrfToken, UpdatedRequestBody="null", genericError, tcid, testName,suiteName;
    public static String[] endpoint_action, JsonFilePath;
    public static int oraclePort, randNo;
    public static Connection con;
    public boolean isOtpEnabled = false;
    public static boolean persistState = false;
    public static HashMap<String, Response> depmap = new HashMap<>();

    UpdateEndpoint endpoint = new UpdateEndpoint();
    UpdateRequestBody request = new UpdateRequestBody();
    Json_Reader requestJson = new Json_Reader();

    // Get the required data from config.properties
    @BeforeSuite
    public void beforeSuite(ITestContext ctx) {

        try {
        	
           HomeDir = System.getProperty("user.dir");
            PropertyConfigurator.configure("Properties"+File.separator+"log4j.properties");
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream("Properties"+File.separator+"config.properties");
            prop.load(input);
            logger.info("**====**"+"SuiteName:" + ctx.getSuite().getName());
            suiteName=ctx.getSuite().getName();
            TestSteps = "Sheet1";
            loginType = prop.getProperty("login");
            dateformat = prop.getProperty("DateFormat");
            timeformat = prop.getProperty("TimeFormat");
            dBMachineName = prop.getProperty("dBMachineName");
            oraclePort = Integer.parseInt(prop.getProperty("oraclePort"));
            sid = prop.getProperty("sid");
            dBUserName = prop.getProperty("dBUserName");
            dBPassword = prop.getProperty("dBPassword");
            RestAssured.baseURI = prop.getProperty("BaseURI");
            RestAssured.port = 2345;
            RestAssured.basePath = prop.getProperty("BasePath");
            URL = prop.getProperty("BaseURI");
            userName = prop.getProperty("UserName");
            password = prop.getProperty("Password");

            ScriptPath = HomeDir+File.separator+"TestScripts"+File.separator;
            logger.info("**====**Home Path is : " + HomeDir);
            logger.info("**====**Script Path is : " + ScriptPath);
            logger.info("**====**URL : " + URL);

            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + dBMachineName + ":" + oraclePort + ":" + sid,
                    dBUserName, dBPassword);

            // Setting the date format specified in the config property
            Statement stmt = XMLSuite.con.createStatement();
            ResultSet rs = stmt.executeQuery("UPDATE SI_LOCALES  SET DATE_FORMAT = '" + dateformat + "',TIME_FORMAT = '"
                    + timeformat + "' WHERE LOCALE_STRING = 'en_US'");
            con.commit();

            if (prop.getProperty("isOtpEnabled").equalsIgnoreCase("Y")
                    || prop.getProperty("isOtpEnabled").equalsIgnoreCase("true")
                    || prop.getProperty("isOtpEnabled").equalsIgnoreCase("yes")) {
                isOtpEnabled = true;
            }

            if (loginType.equalsIgnoreCase("basic")) {
                String jSessionCsrfId[] = LoginSimulator.getSessionId(URL, userName, password, isOtpEnabled);
                jSessionId = (String) jSessionCsrfId[0];
                logger.info("**====**Jsession Id : " + jSessionId);
                csrfToken = (String) jSessionCsrfId[1];
                logger.info("**====**csrfToken : " + csrfToken);
            } else if (loginType.equalsIgnoreCase("oauth")) {
                org.json.JSONObject bearerToken = Oauth.generate_access_token(URL, userName, password);
                accessToken = JsonPath.read(bearerToken.toString(), "$.access_token");
                System.out.println("accessToken" + accessToken);
            }

            Random rndNumbers = new Random();
            randNo = rndNumbers.nextInt(9999);
            logger.info("**====**Random Number for this Execution : " + randNo);
            ExtentReport.startReport();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.BeforeSuite()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
    }

    @AfterMethod
    public void getResult(ITestResult result) {
        try {
            ExtentReport.getResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.AfterMethod()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
    }

    @AfterSuite
    public void afterSuite() {
        try {
            con.close();
            ExtentReport.endReport();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.AfterSuite()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
    }

    // Get the file name from testname parameter in .xml file
    @BeforeTest
    @Parameters({"testname"})
    public void beforeTest(String testname) {
        try {
            fileName = testname;
            JsonFilePath = fileName.split("/");
            FolderName = JsonFilePath[1];
            JsonHome = ScriptPath;
            SchemaHome = ScriptPath;
            logger.info("**====**Json Home path : " + JsonHome);
            logger.info("**====**Schema Home path : " + SchemaHome);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.BeforeTest()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
    }

    @AfterTest
    public void afterTest() {
        Iterator it = depmap.entrySet().iterator();
        TreeMap<String, Object> sorted = new TreeMap<>();
        sorted.putAll(depmap);
        if (sorted.size() > 0) {
            logger.info("==========================================================================================");
            for (Map.Entry<String, Object> entry : sorted.entrySet()) {
                logger.info("Key = " + entry.getKey() + ", Value = " + ((Response) entry.getValue()).asString());
                logger.info("==========================================================================================");
            }
        } else {
            logger.info("Dependency doesnt exixts in Map");
        }
    }

    @BeforeTest
    public void startTest(ITestContext testContext) {
        try {
            testName = testContext.getName();
            System.out.println(testContext.getName());
            MethodName = testContext.getName();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.BeforeTest()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
    }

    // Get the data for the teststeps
    @DataProvider(name = "excelData")
    public Object[][] createData() {
        try {
            cases = new Xls_Reader(ScriptPath + fileName);
            logger.info("**====**TestScript : " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
            genericError = "Exception Caused in XMLSuite.BeforeTest()</br>" + "<b>" + e.toString() + "</b>";
            ExtentReport.failTest();
        }
        return ExcelUtil.getData(TestSteps, cases);
    }

    // Check if the test case is executable and execute based on the Runmode
    @Test(dataProvider = "excelData")
    public void tests(String tCID, String description, String endPointAction, String requestBody,
                      String requestBodyParam, String queryParam, String schemaAssertion, String responseBodyAssertion,
                      String databaseAssertion, String runMode, String persist) throws Exception {

        logger.info("==========================********************=========================");
        logger.info("**====**TestCase ID : " + tCID);
        logger.info("**====**TestCase Description : " + description);
        logger.info("**====**EndPoint and Action: " + endPointAction);
        logger.info("**====**RequestBody: " + requestBody);
        logger.info("**====**RequestBodyParam: " + requestBodyParam);
        logger.info("**====**QueryParam: " + queryParam);
        logger.info("**====**SchemaAssertion: " + schemaAssertion);
        logger.info("**====**ResponseBodyAssertion: " + responseBodyAssertion);
        logger.info("**====**DataBaseAsertion: " + databaseAssertion);
        logger.info("**====**RunMode: " + runMode);
        logger.info("**====**Persist: " + persist);
        // this tcid is used is all RequestMethods for assertion Purpose
        tcid = tCID;

        if (!persist.isEmpty() && !persist.equalsIgnoreCase("n") && !persist.equals("null")) {
            persistState = true;
        } else {
            persistState = false;
            logger.info("Persistence mode is not specified");
        }
        System.out.println("Test Case No:" + tcid);
        ExtentReport.beforeTest(MethodName + ":" + tcid + "." + description);
        int rowNum = ExcelUtil.getRowNum(TestSteps, tCID, cases);

        if ((csrfToken == null) || (jSessionId == null)) {
            logger.info("**====**Skipping the test as application is not accessible/Authentications is failed");
            ExtentReport.skipTest("Skipping the test as application is not accessible/Authentications is failed");
            throw new SkipException("Skipping the test as application is not accessible/Authentications is failed");
        } else if (!ExcelUtil.isExecutable(TestSteps, rowNum, cases)) {
            logger.info("**====**Skipping the testcase Runmode is NO");
            logger.info(
                    "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            ExtentReport.skipTest("Skipping the testcase Runmode is NO");
            throw new SkipException("Skipping the testcase Runmode is NO");
        } else {
            // Update endPoint
        	try{
				// Update endPoint
				endpoint_action = endPointAction.split("~");
				}catch(Exception e){
					logger.info("**====**Action not specified");
					logger.info(
							"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					 ExtentReport.failTest();
				} endPoint = endpoint_action[0];
            operation = endpoint_action[1];
            logger.info("**====**EndPoint : " + endPoint);
            logger.info("**====**RequestMethod : " + operation);
            logger.info("==========================********************=========================");
            logger.info("**====**Executing test : " + tCID);
            if (!queryParam.isEmpty() && !queryParam.equalsIgnoreCase("null")) {
                UpdatedEndpoint = endpoint.update(endPoint, queryParam);
            } else {
                UpdatedEndpoint = endPoint;
                logger.info("**====**Endpoint Update not required");
            }
            // Get RequestBody from Json File or from form Name and Update
            // the
            // same if required
            if (!requestBody.isEmpty() && !requestBody.equalsIgnoreCase("null")) {
                File file = new File(requestBody);
                String fileName = file.getName();
                String filename = null;
                if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                    filename = fileName.substring(fileName.lastIndexOf(".") + 1);
                } else {
                    filename = requestBody;
                }

                if (filename.equalsIgnoreCase("json")) {
                    // Read form json file
                    restRequestBody = requestJson.textFileReader(JsonHome + File.separator + requestBody);
                    // Updaterequest Body
                    UpdatedRequestBody = request.updaterequestBody(restRequestBody, requestBodyParam);
                } else if (filename.equalsIgnoreCase("zip")) {
                    UpdatedRequestBody = requestBody;
                } else {
                    restRequestBody = requestJson.formJson(UpdatedEndpoint, requestBody, requestBodyParam);
                    JSONParser parser = new JSONParser();
                    UpdatedRequestBody = (String) parser.parse(restRequestBody);
                }

            } else {
                if (!requestBodyParam.isEmpty() && !requestBodyParam.equalsIgnoreCase("null")) {
                    UpdatedRequestBody = request.updaterequestParam(requestBodyParam);
                }
                logger.info("**====**Request body Update not Required");
            }
            if (operation.equalsIgnoreCase("get") || operation.equalsIgnoreCase("post")
                    || operation.equalsIgnoreCase("put") || operation.equalsIgnoreCase("delete")
                    || operation.equalsIgnoreCase("sort") || operation.equalsIgnoreCase("upload")
                    || operation.equalsIgnoreCase("download") || operation.equalsIgnoreCase("findcreate")) {
                executeTests(UpdatedEndpoint, operation, UpdatedRequestBody, "json", queryParam);
            } else {
                logger.info("**====** Request Method type is not defined : " + operation);
            }

        }

    }

    // Execute test cases
    public static void executeTests(String UpdatedEndpoint, String operation, String UpdatedRequestBody,
                                    String contentType, String queryParam) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        String className = null;
        String[] argList = new String[0];

        switch (operation) {
            case "get":
                logger.info("**====**Calling GenericGet class");
                className = "requestmethod.GenericGet";
                argList = new String[]{UpdatedEndpoint, contentType};
                break;
            case "post":
                logger.info("**====**Calling GenericPost class");
                className = "requestmethod.GenericPost";
                argList = new String[]{UpdatedEndpoint, String.valueOf(UpdatedRequestBody), contentType};
                break;
            case "put":
                logger.info("**====**Calling GenericPut class");
                className = "requestmethod.GenericPut";
                argList = new String[]{UpdatedEndpoint, String.valueOf(UpdatedRequestBody), contentType};
                break;
            case "delete":
                logger.info("**====**Calling GenericDelete class");
                className = "requestmethod.GenericDelete";
                argList = new String[]{UpdatedEndpoint, String.valueOf(UpdatedRequestBody), contentType};
                break;
            case "sort":
                logger.info("**====**Calling GenericSort class");
                className = "requestmethod.GenericSort";
                argList = new String[]{UpdatedEndpoint, queryParam, contentType};
                break;
            case "upload":
                logger.info("**====**Calling Upload class");
                className = "requestmethod.Upload";
                argList = new String[]{UpdatedEndpoint, UpdatedRequestBody, "multipart"};
                break;
            case "download":
                logger.info("**====**Calling dowmload class");
                className = "requestmethod.Download";
                argList = new String[]{UpdatedEndpoint, UpdatedRequestBody, "download"};
                break;
            case "findcreate":
                logger.info("**====**Calling GenericFind class");
                className = "requestmethod.GenericFind";
                argList = new String[]{UpdatedEndpoint, String.valueOf(UpdatedRequestBody), contentType};
                break;
            default:
                logger.info("**====**Invalid Request Method");
        }
        TestDriver.perform(className, "getResponse", argList);
        logger.info(
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");


    }

}
