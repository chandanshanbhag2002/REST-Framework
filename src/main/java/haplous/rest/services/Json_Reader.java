package haplous.rest.services;

import com.jayway.restassured.response.Response;

import haplous.rest.init.XMLSuite;
import haplous.rest.requestmethod.GenericGet;
import haplous.rest.requestmethod.GenericPost;

import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class Json_Reader {
	public final Logger logger = Logger.getLogger(Json_Reader.class);
	public String textStringReader(String fileName){
		BufferedReader bufferedReader;
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			while((line =bufferedReader.readLine())!=null){
				stringBuffer.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		logger.info(stringBuffer);
		return 	stringBuffer.toString();
	}
	
	public String textFileReader(String fileName){
		String str = null;
		try {
		   str = FileUtils.readFileToString(new File(fileName));
		   logger.info("**====**Json Read From file :" +str);
		 } catch (IOException e) {
		    e.printStackTrace();
		}
		return str;
	}

	public void writeJsonToFile(String fileName,String content) throws IOException {
		try (

                Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(XMLSuite.ScriptPath+XMLSuite.FolderName+File.separator+"FormJson"+File.separator+fileName),
                        "utf-8"));) {
            writer.write(content);
        } catch (IOException ex) {
            logger.error("Error while writeJson" + ex);
        }
    }

	public String formJson(String endPoint,String requestBody,String requestBodyParam) throws Exception {
	    GenericPost post = new GenericPost();
	    GenericGet get = new GenericGet();
        JSONParser parser = new JSONParser();
        Response r = post.getResponse(endPoint,requestBodyParam,"json");
        String res = r.asString();
        JSONObject resJsonObj = (JSONObject) parser.parse(res);
        String tid = (String) resJsonObj.get("assignmentId");
        String formBodyEndpoint = "/tasks/"+tid+"/form";
        Response formRes = get.getResponse(formBodyEndpoint,"json");
        String formResponse = formRes.asString();
        writeJsonToFile(requestBody+".json",formResponse);
        logger.info("Form response Body : "+formResponse);

	    return formResponse;
    }
	
	
}
