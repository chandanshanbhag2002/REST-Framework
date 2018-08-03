package haplous.rest.services;

import java.io.File;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import haplous.rest.assertion.DatabaseAssertion;
import haplous.rest.assertion.ResponseBodyAssertion;
import haplous.rest.assertion.SchemaAssertion;
import haplous.rest.assertion.StatusCodeAssertion;
import haplous.rest.init.XMLSuite;
import haplous.rest.requestmethod.GenericSort;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;


public class ExtentReport {

    public static ExtentReports extent;
    public static ExtentTest logger;
    public static String message = "";

    public static void startReport() {
        extent = new ExtentReports(XMLSuite.HomeDir +File.separator+"extent-output"+File.separator+"extent"+"_"+XMLSuite.suiteName+".html", true);
        extent.loadConfig(new File(XMLSuite.HomeDir +File.separator+"Properties"+File.separator+ "extent-config.xml"));
        extent.addSystemInfo("Host Name",XMLSuite.URL);
        extent.addSystemInfo("User Name",XMLSuite.userName) ;
        extent.addSystemInfo("Password",XMLSuite.password);
        extent.addSystemInfo("Database Host",XMLSuite.dBMachineName);
        extent.addSystemInfo("Db UserName",XMLSuite.dBUserName);
        extent.addSystemInfo("Db Password",XMLSuite.dBPassword);
        extent.addSystemInfo("DB Sid",XMLSuite.sid);
        extent.addSystemInfo("Random-Number",Integer.toString(XMLSuite.randNo));




   }

    public static void passTest() {
        Assert.assertTrue(true);
    }

    public static void failTest() {
        Assert.assertTrue(false);
    }

    public static void skipTest(String exceptionmsg) {
        throw new SkipException(exceptionmsg);
    }

    public static void endTest() {
        extent.endTest(logger);
    }

    public static void beforeTest(String className) {

        logger = extent.startTest(className);
    }

    public static void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String failuremsg = message();
            logger.log(LogStatus.FAIL, failuremsg);

        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.log(LogStatus.SKIP, "Test Case Skipped because " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.log(LogStatus.PASS, "Test Case passed");
        }
        message="";

    }

    public static String message(){

        message = "Test Case failed with <br>";
       if( StatusCodeAssertion.response.getStatusCode() != 200 ){
           message = message + "Status code :"+ StatusCodeAssertion.response.getStatusCode() + "<br> Status line :" + StatusCodeAssertion.response.getStatusLine() + "<br> Response string :" + StatusCodeAssertion.response.asString();
       }
       if( DatabaseAssertion.DatabaseAssert != null){
           message = message + "<b>Database assertion failure : </b>"+DatabaseAssertion.DatabaseAssert;
           DatabaseAssertion.DatabaseAssert = null;
       }
       if(ResponseBodyAssertion.ResponseAssert != null){
            message = message + "<b>Response assertion failure : </b>"+ResponseBodyAssertion.ResponseAssert;
           ResponseBodyAssertion.ResponseAssert = null;
        }
        if(SchemaAssertion.SchemaAssert != null){
            message = message + "<b>Schema assertion failure : </b>"+SchemaAssertion.SchemaAssert;
            SchemaAssertion.SchemaAssert = null;
        }
        if(GenericSort.SortingAssert != null){
            message = message + "<b>Sorting failure : </b>"+GenericSort.SortingAssert;
            GenericSort.SortingAssert = null;
        }

        if(XMLSuite.genericError != null){
            message = message + "</br><b>Configuration failure : </b>"+XMLSuite.genericError;
            XMLSuite.genericError = null;
        }

        return message;
    }

    public static void endReport() {

        extent.flush();
    }

    
}
