package haplous.rest.services;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Iterator;
import java.util.Map;

public class UpdateRequestBody {
	public final Logger logger = Logger.getLogger(UpdateRequestBody.class);

	public String updaterequestBody(String requestBody, String requestBodyparam) throws Exception {
		String reqobj = RestAPI.appendRandomNo(requestBodyparam);
		JsonParser j = new JsonParser();
		Map<String, Object> updatedRequestBody = j.parse(reqobj);
		JSONObject jk = new JSONObject();
		jk.putAll(updatedRequestBody);
		String reqbody = jk.toString();
		JSONParser parser = new JSONParser();
		JSONObject bodyJsonObj = (JSONObject) parser.parse(requestBody);
		if (!reqbody.isEmpty()) {
			// Traverse json object
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			JsonNode rootNode = mapper.readTree(reqbody);
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
			// Update json using mutable json
			Map<String, Object> userData = null;
			userData = new ObjectMapper().readValue(bodyJsonObj.toJSONString(), Map.class);
			MutableJson json = new MutableJson(userData);
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				json.update(field.getKey(), field.getValue());
			}
			bodyJsonObj.putAll(json.map());
			logger.info("**====**Updated Request body : " + bodyJsonObj.toString());
		} else {
			logger.info("**====**Request body Update not Required");
		}

		return bodyJsonObj.toString();
	}

	public String updaterequestParam(String requestBodyparam) throws Exception {
		String reqobj = RestAPI.appendRandomNo(requestBodyparam);
		JSONObject jk = new JSONObject();
		JsonParser j = new JsonParser();
		
		if (reqobj.startsWith("[")) {
			String res=reqobj.substring(1,reqobj.length()-1);
			Map<String, Object> updatedRequestBody = j.parse(res);
			jk.putAll(updatedRequestBody);
			res=jk.toString();
			res="["+res+"]";
			logger.info("**====**Updated Request body : " + res);
			return res;
		} else {
			
			Map<String, Object> updatedRequestBody = j.parse(reqobj);
			jk.putAll(updatedRequestBody);
			logger.info("**====**Updated Request body : " + jk.toString());
			return jk.toString();
		}
	}
}
