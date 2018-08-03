package haplous.rest.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.io.FileOutputStream;

import com.fasterxml.jackson.databind.JsonNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.init.XMLSuite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class RestAPI {
    public static RequestSpecification request;
    public static SoftAssert softAssert = new SoftAssert();
    private static final int BUFFER_SIZE = 4096;

    //Function to get DB connection
    public static Connection getconn() throws IOException, ClassNotFoundException, SQLException {

        Properties prop = new Properties();
        InputStream input = new FileInputStream("config.properties");
        prop.load(input);
        String dBMachineName = prop.getProperty("dBMachineName");
        int oraclePort = Integer.parseInt(prop.getProperty("oraclePort"));
        String sid = prop.getProperty("sid");
        String dBUserName = prop.getProperty("dBUserName");
        String dBPassword = prop.getProperty("dBPassword");

        Class.forName("oracle.jdbc.driver.OracleDriver");

        //step2 create  the connection object
        Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@" + dBMachineName + ":" + oraclePort + ":" + sid, dBUserName, dBPassword);

        return con;

    }

    public static void closeConnection(Connection con) throws SQLException {
        con.close();
    }


    //Function to generate random number
    public static String appendRandomNo(String value) {
        int sizeOfRandNo = 0;
        int sizeToAppend = 0;
        sizeOfRandNo = String.valueOf(XMLSuite.randNo).length();
        List<Integer> randIdentifier = new ArrayList<>();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '+') {
                if (value.charAt(i - 1) == '!') {
                    System.out.println("need not append rand no");
                    value = value.substring(0, i - 1) + value.substring(i);
                } else {
                    randIdentifier.add(i);
                }
            }
        }
        StringBuffer appendrand = new StringBuffer(value);
        for (Integer j : randIdentifier) {
            appendrand.replace((j + sizeToAppend), (j + sizeToAppend + 1), String.valueOf(XMLSuite.randNo));
            sizeToAppend = sizeToAppend + (sizeOfRandNo - 1);
        }
        return appendrand.toString();
    }

    //Function to set header for the HTTP request
    public static RequestSpecBuilder setHeader(String contentType) {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        Map<String, String> cooky = new HashMap<String, String>();

        String csrfToken = XMLSuite.csrfToken;

        System.out.println(csrfToken);
        System.out.println("jsession received : " + XMLSuite.jSessionId);

        builder.addHeader("X-Csrf-Token", csrfToken);
        cooky.put("JSESSION_ID", XMLSuite.jSessionId);
        builder.addCookies(cooky);

        if (contentType.equalsIgnoreCase("json")) {
            builder.setContentType("application/json; charset=UTF-8");
            builder.addHeader("accept", "application/json");

        } else if (contentType.equalsIgnoreCase("multipart")) {
            builder.setContentType("multipart/form-data");
            builder.addHeader("accept", "application/json");

        } else if (contentType.equalsIgnoreCase("xml")) {
            builder.setContentType("application/xml; charset=UTF-8");
            builder.addHeader("accept", "application/xml");

        }
        return builder;
    }


    //Function to compare json objects/arrays
    public static void compare(Object obj1, Object obj2) {
        if (obj1 instanceof JSONObject) {
            if (obj2 instanceof JSONObject) {
                compareJSONObject((JSONObject) obj1, (JSONObject) obj2);
            } else {
                System.out.println("Error found***");
            }
        } else if (obj1 instanceof JSONArray) {
            if (obj2 instanceof JSONArray) {
                JSONArray arr1 = (JSONArray) obj1;
                JSONArray arr2 = (JSONArray) obj2;
                for (int i = 0; i < arr2.size(); i++) {
                    compare(arr1.get(i), arr2.get(i));
                }
            } else {
                System.out.println("Error found*");
            }
        } else {
            System.out.println("Compare " + obj1 + " == " + obj2);
            Assert.assertEquals(obj1, obj2);
        }
    }

    private static void compareJSONObject(JSONObject obj1, JSONObject obj2) {
        for (Object key : obj2.keySet()) {
            compare(obj1.get(key), obj2.get(key));
        }
    }

    //Function to read file
    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static void downloadFile(InputStream inputStream, String saveFilePath)
            throws IOException {
        System.out.println(saveFilePath);
        FileOutputStream outputStream = new FileOutputStream(saveFilePath);

        // Process the response
        BufferedReader reader;
        int i = -1;
        while ((i = inputStream.read()) != -1) {
            outputStream.write(i);
        }
        outputStream.close();
        inputStream.close();

        System.out.println("File downloaded");
    }


    public static String runSqlQuery(String query) throws Exception {

        int count = 0;
        String[] result = null;
        Connection con =XMLSuite.con;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            count++;
        }

        if (count == 1) {
            rs = stmt.executeQuery(query);
            result = new String[count];
            while (rs.next()) {
                for (int i = 0; i < count; i++) {
                    result[i] = rs.getString(1);
                }
            }
        }

        return result[0].toString();
    }

}


