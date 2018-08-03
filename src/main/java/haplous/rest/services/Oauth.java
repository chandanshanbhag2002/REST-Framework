/*----------
 * 
 * Author:  chandan.shanbhag@metricstream.com
 * 
 * ---------*/
package haplous.rest.services;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.response.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;


public class Oauth {

	public static JSONObject generate_access_token(String URL, String username, String password) {
		JSONObject res=null;
		try {
			RestAssured.useRelaxedHTTPSValidation();
			Header header = null;
			Map<String, String> cooky = new HashMap<String, String>();
			Map<String, String> oauthcooky = new HashMap<String, String>();
			String redirect_url = null;
			System.out.println(URL+username+password);
			Response response = given().redirects().follow(false).when()
					.get(URL + "/metricstream/oauth2/authorize?response_type=initial_token");
			redirect_url = response.getHeader("Location");
			System.out.println(redirect_url);
			response = given().when().redirects().follow(false).get(URL + redirect_url);
			redirect_url = response.getHeader("Location");
			System.out.println(redirect_url);
			header = new Header("Content-Type", "application/x-www-form-urlencoded");
			oauthcooky.put("response_type", "initial_token");
			response = given().when().redirects().follow(false).formParam("username", username)
					.formParam("password", password).header(header).cookies(oauthcooky).request()
					.post(URL + "/metricstream/auth/basic?locale=en_US");
			System.out.println(response.asString() + ":" + response.getStatusLine() + ":" + response.getCookies());
			redirect_url = response.getHeader("Location");
			System.out.println(redirect_url);
			header = new Header("accept", "text/html");
			cooky.put("JSESSION_ID", response.getCookie("JSESSION_ID"));
			cooky.put("Csrf-Token", response.getCookie("Csrf-Token"));
			cooky.put("response_type", "initial_token");
			Response initial_token_res = given().redirects().follow(false).header(header).cookies(cooky)
					.get(URL + redirect_url);
			redirect_url = initial_token_res.getHeader("Location");
			System.out.println(redirect_url);
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(redirect_url), "UTF-8");
			String initial_token = params.get(0).toString().split("=")[1];
			System.out.println("Initial Token:" + initial_token);

			Response client_res = given()
					.post(URL + "/metricstream/oauth2/register?initial_access_token=" + initial_token);
			 res = new JSONObject(client_res.getBody().asString());
			String client_id = res.get("client_id").toString();
			String client_secret = res.get("client_secret").toString();
			System.out.println("Client_ID:" + client_id + "\n" + "Client_Secret:" + client_secret);

			Response code_res = given().redirects().follow(false).when()
					.get(URL + "/metricstream/oauth2/authorize?response_type=code&client_id=" + client_id);
			redirect_url = code_res.getHeader("Location");
			System.out.println(redirect_url);
			header = new Header("Content-Type", "application/x-www-form-urlencoded");
			Response response2 = given().when().redirects().follow(false).formParam("username", username)
					.formParam("password", password).header(header).cookies(oauthcooky).request()
					.post(URL + "/metricstream/auth/basic?locale=en_US");
			redirect_url = response2.getHeader("Location");
			System.out.println(redirect_url);
			header = new Header("accept", "text/html");
			cooky.put("JSESSION_ID", response2.getCookie("JSESSION_ID"));
			cooky.put("Csrf-Token", response2.getCookie("Csrf-Token"));
			cooky.put("response_type", "code");
			cooky.put("client_id", client_id);
			Response access_token_res = given().redirects().follow(false).header(header).cookies(cooky)
					.get(URL + redirect_url);
			redirect_url = access_token_res.getHeader("Location");
			
			params = URLEncodedUtils.parse(new URI(redirect_url), "UTF-8");
			String code = params.get(0).toString().split("=")[1];

			Response res_access_refesh = given()
					.post(URL + "/metricstream/oauth2/token?grant_type=authorization_code&code=" + code + "&client_id="
							+ client_id + "&client_secret=" + client_secret);
			res = new JSONObject(res_access_refesh.getBody().asString());
			String refresh_token = res.get("refresh_token").toString();

			Response res_bearer_token = given()
					.post(URL + "/metricstream/oauth2/token?grant_type=refresh_token&refresh_token=" + refresh_token
							+ "&client_id=" + client_id + "&client_secret=" + client_secret);
			res = new JSONObject(res_bearer_token.getBody().asString());
			System.out.println(res.toString());
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return res;
	}

	/*public static void main(String args[]) {
		Oauth oauth = new Oauth();
		JSONObject j=oauth.generate_access_token("https://autoauth.rnd.metricstream.com:443", "SYSTEMI", "welcome*12");
		System.out.println(j.toString());
	}*/
}
