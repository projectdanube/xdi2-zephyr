package xdi2.core.impl.zephyr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class ZephyrUtils {
	
	public static String doGet(String url) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		System.out.println("Using URL : " + url );
		request.addHeader("Content-Type", "application/json");
		HttpResponse response = httpclient.execute(request);
		System.out.println("Statusline : " + response.getStatusLine());
		InputStream data = response.getEntity().getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
		String responeLine;
		StringBuilder responseBuilder = new StringBuilder();
		while ((responeLine = bufferedReader.readLine()) != null) {
			responseBuilder.append(responeLine);
		    }
		    return responseBuilder.toString();
	}
	
	public static void doPut(String url, String Key, String Value) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPut request = new HttpPut(url);
		request.addHeader("Content-Type", "application/json");
		StringEntity input = new StringEntity("{\"" + Key +"\":\""+ Value +"\"}");
		//input.setContentType("application/json");

		request.setEntity(input);
		//request.addHeader("Cookie", "token=SECRET");
		//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    //nameValuePairs.add(new BasicNameValuePair("foo","shrey"));
	    //JSONArray jsonList = JSONArray.fromObject();
		//request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		System.out.println("Using URL : " + url );
		
		HttpResponse response = httpclient.execute(request);
		System.out.println("Statusline : " + response.getStatusLine());
		
	}
	
	public static String doDelete(String url) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpDelete request = new HttpDelete(url);
		System.out.println("Using URL : " + url );
		
		HttpResponse response = httpclient.execute(request);
		System.out.println("Statusline : " + response.getStatusLine());
		InputStream data = response.getEntity().getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
		String responeLine;
		StringBuilder responseBuilder = new StringBuilder();
		while ((responeLine = bufferedReader.readLine()) != null) {
			responseBuilder.append(responeLine);
		    }
		    return responseBuilder.toString();
	}
	
	public static String mapGraph(String jsonObject)
	{
		
		try {
			JSONObject jsonGraph  = new JSONObject(jsonObject);
			//JSONObject  menu = jObject.getJSONObject("menu");
			
			Map<String,String> map = new HashMap<String,String>();
		    Iterator<?> iter = jsonGraph.keys();
		    while(iter.hasNext()){
		        String key = (String)iter.next();
		        String value = jsonGraph.getString(key);
		        map.put(key,value);
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	
	}

}
