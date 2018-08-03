package haplous.rest.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import haplous.rest.init.XMLSuite;

public class JsonParser {
	public final Logger logger = Logger.getLogger(JsonParser.class);

	public Map<String, Object> parse(String jsonStr) throws Exception {

		Map<String, Object> result = null;

		if (null != jsonStr) {
			try {
				
				JSONObject jsonObject = new JSONObject(jsonStr);
				result = parseJSONObject(jsonObject);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // if (null != jsonStr)

		return result;
	}

	private Object parseValue(Object inputObject) throws Exception {
		Object outputObject = null;
		String setDate = null;
		DateUtil dt = new DateUtil();
		try{
		if (null != inputObject) {

			if (inputObject instanceof JSONArray) {
				outputObject = parseJSONArray((JSONArray) inputObject);
			} else if (inputObject instanceof JSONObject) {
				outputObject = parseJSONObject((JSONObject) inputObject);
			} else if (inputObject instanceof String || inputObject instanceof Boolean
					|| inputObject instanceof Integer) {

				outputObject = inputObject;
				if (inputObject.toString().contains("currentdate")) {
					if (inputObject.toString().contains(",")) {
						String[] Days = inputObject.toString().split(",");
						if (Days[1].contains("-")) {
							setDate = dt.futureDate(Integer.parseInt(Days[1]));
							logger.info("**====** Setting Past date :" + setDate);
						} else {
							setDate = dt.futureDate(Integer.parseInt(Days[1]));
							logger.info("**====** Setting future date :" + setDate);
						}
					} else {
						setDate = dt.getDateAndTime();
						logger.info("**====** Setting Current date :" + setDate);
					}
					outputObject = setDate;
				} else if (inputObject.toString().startsWith("!")) {
					outputObject = inputObject.toString().replaceAll("!", "");
				} else if (inputObject.toString().startsWith("select") || inputObject.toString().startsWith("SELECT")
						|| inputObject.toString().startsWith("Select")) {

					Object result = RestAPI.runSqlQuery(inputObject.toString());
					if (result instanceof Integer) {
						outputObject = Integer.parseInt((String) result);
					} else if (result instanceof String) {
						outputObject = (String) result;
					} else if (result instanceof Boolean) {
						outputObject = (String) result;
					}
					 
				}else if(inputObject.toString().startsWith("|")){
					String[] dep = inputObject.toString().split("~");
					String tcid = dep[1];
					String path = dep[2];
					logger.info("Dependent Test Case-"+tcid);
					logger.info("Path to fetch data form dependent TC-" + path);
					try {
						Object temp = JsonPath.read(XMLSuite.depmap.get(tcid.toLowerCase()).asString(), path);
						outputObject = String.valueOf(temp);
					} catch (Exception e) {
						logger.info(e.toString());
					}
					
				}
			}

		}
		}catch(Exception e){
			logger.info("Exception caused while parsing json\n"+e.toString()+"\n"+inputObject.toString());
			logger.info(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            ExtentReport.failTest();
		}

		return outputObject;
		
	}

	private List<Object> parseJSONArray(JSONArray jsonArray) throws Exception {

		List<Object> valueList = null;

		if (null != jsonArray) {
			valueList = new ArrayList<Object>();

			for (int i = 0; i < jsonArray.length(); i++) {
				Object itemObject = jsonArray.get(i);
				if (null != itemObject) {
					valueList.add(parseValue(itemObject));
				}
			} // for (int i = 0; i <jsonArray.length(); i++)
		} // if (null != valueStr)

		return valueList;
	}

	private Map<String, Object> parseJSONObject(JSONObject jsonObject) throws Exception {

		Map<String, Object> valueObject = null;
		if (null != jsonObject) {
			valueObject = new HashMap<String, Object>();

			Iterator<String> keyIter = jsonObject.keys();
			while (keyIter.hasNext()) {
				String keyStr = keyIter.next();
				Object itemObject = jsonObject.opt(keyStr);
				if (null != itemObject) {
					valueObject.put(keyStr, parseValue(itemObject));
				} // if (null != itemValueStr)

			} // while (keyIter.hasNext())
		} // if (null != valueStr)

		return valueObject;
	}

}
