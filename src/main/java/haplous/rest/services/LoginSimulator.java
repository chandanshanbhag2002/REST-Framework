package haplous.rest.services;

import static com.jayway.restassured.RestAssured.given;

import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import haplous.rest.init.XMLSuite;

public class LoginSimulator {
	public static final Logger logger = Logger.getLogger(LoginSimulator.class);

	public static String[] getSessionId(String URL, String userName, String password, boolean isOtpEnabled)
			throws Exception {
		RestAssured.useRelaxedHTTPSValidation();
		String jSessionCsrfId[] = new String[2];
		if (isOtpEnabled) {
			String login = URL + "metricstream/auth/signin.jsp";
			System.out.println("Login:" + login);
			logger.info("Login:" + login);
			String basic = URL + "metricstream/auth/basic";
			System.out.println("basic:" + basic);
			logger.info("basic:" + basic);

			Response response = given().get(login);
			Assert.assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_OK, "Login page loading is failed");

			// login:
			Cookies cookie = new Cookies();
			Header header = new Header("Content-Type", "application/x-www-form-urlencoded");

			response = given().redirects().follow(false).formParam("username", userName).formParam("password", password)
					.header(header).request().post(basic);
			// Assert.assertEquals(response.getStatusCode(),
			// HttpURLConnection.HTTP_MOVED_TEMP, "Authentication Failed");
			System.out.println(response.getHeader("Location"));
			jSessionCsrfId[0] = response.getCookie("JSESSION_ID");
			jSessionCsrfId[1] = response.getCookie("Csrf-Token");
			RequestSpecBuilder builder = new RequestSpecBuilder();
			Map<String, String> cooky = new HashMap<String, String>();
			builder.addHeader("X-Csrf-Token", jSessionCsrfId[1]);
			cooky.put("JSESSION_ID", jSessionCsrfId[0]);
			builder.addCookies(cooky);
			RequestSpecification requestSpec = builder.build();

			// logger.info(response.asString()+":"+response.getStatusLine()+":"+response.getCookies());
			response = given().redirects().follow(false).spec(requestSpec).get(URL + "metricstream/auth/otp");
			Assert.assertEquals(response.getStatusCode(), 302, "OTP page loading is failed");
			System.out.println(response.getStatusCode());
			System.out.println(response.getHeader("Location"));

			String sharedSecret = null, eventCounter = null;
			Statement stmt = XMLSuite.con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT SHARED_SECRET,EVENT_COUNTER FROM SI_USER_2FA_DETAILS WHERE USER_NAME='" + userName + "'");
			while (rs.next()) {
				sharedSecret = rs.getString("SHARED_SECRET");
				eventCounter = rs.getString("EVENT_COUNTER");
			}

			GetOtp g = new GetOtp();
			String otp = g.getOTP(sharedSecret, eventCounter, 6);
			System.out.println(otp);
			response = given().redirects().follow(false).spec(requestSpec).get(URL + "metricstream/auth/OTP.jsp");
			Assert.assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_OK, "OTP page loading is failed");
			System.out.println(response.getStatusCode());

			response = given().redirects().follow(false).formParam("otp", otp).spec(requestSpec).header(header)
					.request().post(URL + "metricstream/auth/otp");

			System.out.println(response.getHeader("Location"));

			if (response.getHeader("Location").equals("/ui/")) {
				if (jSessionCsrfId[0].isEmpty() || jSessionCsrfId[1].isEmpty()) {
					jSessionCsrfId[0] = response.getCookie("JSESSION_ID");
					jSessionCsrfId[1] = response.getCookie("Csrf-Token");
				}
			} else {
				jSessionCsrfId = null;
			}

		} else {

			String login = URL + "metricstream/auth/signin.jsp";
			logger.info("**====**Login:" + login);
			String basic = URL + "metricstream/auth/basic";
			logger.info("**====**basic:" + basic);
			Response response = given().get(login);
			Assert.assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_OK, "Login page loading is failed");

			// login:
			Cookies cookie = new Cookies();
			Header header = new Header("Content-Type", "application/x-www-form-urlencoded");

			response = given().formParam("username", userName).formParam("password", password).header(header).request()
					.post(basic);
			Assert.assertEquals(response.getStatusCode(), HttpURLConnection.HTTP_MOVED_TEMP, "Authentication Failed");
			logger.info(response.asString() + ":" + response.getStatusLine() + ":" + response.getCookies());

			jSessionCsrfId[0] = response.getCookie("JSESSION_ID");
			jSessionCsrfId[1] = response.getCookie("Csrf-Token");
		}
		return jSessionCsrfId;

	}
}
