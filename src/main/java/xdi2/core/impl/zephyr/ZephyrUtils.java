package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ZephyrUtils {

	private static final Logger log = LoggerFactory.getLogger(ZephyrUtils.class);

	private static DefaultHttpClient httpClient;

	static {

		httpClient = new DefaultHttpClient(new BasicClientConnectionManager());
	}

	public static JSONObject doGet(String url) throws IOException {

		HttpGet request = null;
		HttpResponse response = null;

		try {

			request = new HttpGet(url);
			log.debug("HTTP GET: " + url);

			response = httpClient.execute(request);
			log.debug("HTTP GET RESPONSE: " + response.getStatusLine());

			if (HttpStatus.valueOf(response.getStatusLine().getStatusCode()).equals(HttpStatus.NOT_FOUND)) return null;
			if (! HttpStatus.valueOf(response.getStatusLine().getStatusCode()).series().equals(HttpStatus.Series.SUCCESSFUL)) throw new IOException("HTTP error: " + response.getStatusLine().getReasonPhrase());

			String body = EntityUtils.toString(response.getEntity());
			log.debug("HTTP GET BODY: " + body);

			return JSON.parseObject(body);
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public static void doPut(String url, JSONObject json) throws IOException {

		HttpEntity entity = null;
		HttpPut request = null;
		HttpResponse response = null;

		try {

			String body = json.toJSONString();
			log.debug("HTTP PUT BODY: " + body);

			entity = new StringEntity(body, ContentType.create("application/json"));
			log.debug("HTTP PUT ENTITY: " + entity.getContentType());

			request = new HttpPut(url);
			log.debug("HTTP PUT: " + url);
			request.setEntity(entity);

			response = httpClient.execute(request);
			log.debug("HTTP PUT RESPONSE: " + response.getStatusLine());
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public static void doPut(String url, String key, Object value) throws IOException {

		JSONObject json = new JSONObject();
		if (key != null) json.put(key, value);

		doPut(url, json);
	}

	public static void doDelete(String url) throws IOException {

		HttpDelete request = null;
		HttpResponse response = null;

		try {

			request = new HttpDelete(url);
			log.debug("HTTP DELETE: " + url);

			response = httpClient.execute(request);
			log.debug("HTTP DELETE RESPONSE: " + response.getStatusLine());

			EntityUtils.consume(response.getEntity());
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public static String encode(String string) {

		try {

			return URLEncoder.encode(string, "UTF-8").replace("-", "%2D").replace(".", "%2E").replace("%", "-");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static String decode(String string) {

		try {

			return URLDecoder.decode(string.replace("-", "%"), "UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
