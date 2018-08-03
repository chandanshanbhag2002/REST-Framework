package haplous.rest.services;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateEndpoint {
	public final Logger logger = Logger.getLogger(UpdateEndpoint.class);

	public String update(String endPoint, String queryParam) throws Exception {
		String updatedEndpoint = null;
		JSONParser parser = new JSONParser();
		queryParam = RestAPI.appendRandomNo(queryParam);
		JsonParser j = new JsonParser();
		JSONObject jsonObj = new JSONObject();
		if (!queryParam.isEmpty()) {
			jsonObj.putAll(j.parse(queryParam));
			Pattern pattern = Pattern.compile("\\{(.*?)\\}");
			Matcher matcher = pattern.matcher(endPoint);
			while (matcher.find()) {
				try {
					String source = matcher.group(0);
					String destination = jsonObj.get(matcher.group(1)).toString();
					try{
						jsonObj.putAll((Map) jsonObj.get(matcher.group(1)));
						updatedEndpoint = endPoint.replace(source, jsonObj.toJSONString());
					}catch(ClassCastException e){
						updatedEndpoint = endPoint.replace(source, destination);
					}
					endPoint=updatedEndpoint;
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
			;
			logger.info("**====**Updated endPoint : " + updatedEndpoint);
		} else {
			logger.info("**====**Endpoint Update not required");
		}

		return updatedEndpoint;
	}

}
