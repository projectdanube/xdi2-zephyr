package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ZephyrUtils {

	private static final Logger log = LoggerFactory.getLogger(ZephyrUtils.class);

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private static final JsonParser jsonParser = new JsonParser();

	private DefaultHttpClient httpClient;
	private List<String> httpLog;

	public ZephyrUtils() {
		
		this.httpClient = new DefaultHttpClient(new BasicClientConnectionManager());
		this.httpLog = new ArrayList<String> ();
	}

	public JsonObject doGet(String url) throws IOException {

		this.getHttpLog().add("GET " + url);

		HttpGet request = null;
		HttpResponse response = null;

		try {

			request = new HttpGet(url);
			log.debug("HTTP GET: " + url);

			response = this.httpClient.execute(request);
			log.debug("HTTP GET RESPONSE: " + response.getStatusLine());

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) return null;
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new IOException("HTTP error " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());

			String body = EntityUtils.toString(response.getEntity());
			log.debug("HTTP GET BODY: " + body);

			return (JsonObject) jsonParser.parse(new StringReader(body));
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public void doPut(String url, JsonObject jsonObject) throws IOException {

		this.getHttpLog().add("PUT " + url);

		HttpEntity entity = null;
		HttpPut request = null;
		HttpResponse response = null;

		try {

			String body = gson.toJson(jsonObject);
			log.debug("HTTP PUT BODY: " + body);

			entity = new StringEntity(body, ContentType.create("application/json"));
			log.debug("HTTP PUT ENTITY: " + entity.getContentType());

			request = new HttpPut(url);
			log.debug("HTTP PUT: " + url);
			request.setEntity(entity);

			response = this.httpClient.execute(request);
			log.debug("HTTP PUT RESPONSE: " + response.getStatusLine());

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NO_CONTENT) throw new IOException("HTTP error " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public void doDelete(String url) throws IOException {

		this.getHttpLog().add("DELETE " + url);

		HttpDelete request = null;
		HttpResponse response = null;

		try {

			request = new HttpDelete(url);
			log.debug("HTTP DELETE: " + url);

			response = this.httpClient.execute(request);
			log.debug("HTTP DELETE RESPONSE: " + response.getStatusLine());

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NO_CONTENT) throw new IOException("HTTP error " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
		} finally {

			if (response != null) EntityUtils.consume(response.getEntity());
		}
	}

	public DefaultHttpClient getHttpClient() {
	
		return this.httpClient;
	}

	public List<String> getHttpLog() {
	
		return this.httpLog;
	}

	public static String encode(String string) {

		try {

			return URLEncoder.encode(string, "UTF-8").replace("-", "%2D").replace("%", "-");
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
