package JsonTool;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Unmarshaling {
	public String getJson(String clientMsg, Object n){
		JSONParser parser =new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(clientMsg);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String TempObj = (String) obj.get(n);
		return TempObj;
	}


}
