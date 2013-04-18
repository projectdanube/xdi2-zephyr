package xdi2.core.impl.zephyr;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ZephyrUtils {

	private static final Logger log = LoggerFactory.getLogger(ZephyrUtils.class);

	private static HttpClient httpClient;

	static {

		httpClient = new DefaultHttpClient();
	}

	public static JSONObject doGet(String url) throws IOException {

		HttpGet request = new HttpGet(url);
		log.debug("HTTP GET: " + url);
		request.addHeader("Content-Type", "application/json");

		HttpResponse response = httpClient.execute(request);
		log.debug("HTTP GET RESPONSE: " + response.getStatusLine());

		HttpEntity entity = response.getEntity();
		log.debug("HTTP GET ENTITY: " + entity.getContentType());

		String body = EntityUtils.toString(entity);
		log.debug("HTTP GET BODY: " + body);

		return JSON.parseObject(body);
	}

	public static void doPut(String url, JSONObject json) throws IOException {

		String body = json.toJSONString();
		log.debug("HTTP PUT BODY: " + body);

		HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
		log.debug("HTTP PUT ENTITY: " + entity.getContentType());

		HttpPut request = new HttpPut(url);
		log.debug("HTTP PUT: " + url);
		request.setEntity(entity);

		HttpResponse response = httpClient.execute(request);
		log.debug("HTTP PUT RESPONSE: " + response.getStatusLine());
	}

	public static void doPut(String url, String key, Object value) throws IOException {

		JSONObject json = new JSONObject();
		if (key != null) json.put(key, value);

		doPut(url, json);
	}

	public static void doDelete(String url) throws IOException {

		HttpDelete request = new HttpDelete(url);
		log.debug("HTTP DELETE: " + url);

		HttpResponse response = httpClient.execute(request);
		log.debug("HTTP DELETE RESPONSE: " + response.getStatusLine());
	}

	public static String searchJson(String jsonstring, String key) {

		String result = "";

		if (jsonstring != null && jsonstring.length() > 0) {
			String startSearchString = "\"" + key + "\":\"";

			String endSearchString = "\"";

			int startIndex = jsonstring.indexOf(startSearchString)
					+ startSearchString.length();
			int endIndex = jsonstring.indexOf(endSearchString, startIndex);
			if (endIndex - startIndex > 0) {
				result = jsonstring.substring(startIndex, endIndex);
			}
		}

		return result;

	}
}
