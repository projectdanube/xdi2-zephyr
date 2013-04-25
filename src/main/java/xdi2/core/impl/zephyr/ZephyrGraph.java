package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.util.Map.Entry;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.impl.zephyr.util.ZephyrUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private static final JsonParser jsonParser = new JsonParser();

	private String identifier;

	private String dataApi;
	private String oauthToken;
	private Cache cache;
	private ZephyrUtils zephyrUtils;

	private ZephyrContextNode rootContextNode;

	ZephyrGraph(ZephyrGraphFactory graphFactory, String identifier, String dataApi, String oauthToken, Cache cache, ZephyrUtils zephyrUtils) {

		super(graphFactory);

		this.identifier = identifier;

		this.dataApi = dataApi;
		this.oauthToken = oauthToken;
		this.cache = cache;
		this.zephyrUtils = zephyrUtils;

		this.rootContextNode = new ZephyrContextNode(this, null, null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.rootContextNode;
	}

	@Override
	public void close() {

		if (this.getCache() != null) this.getCache().removeAll();
		if (this.getZephyrUtils() != null) this.getZephyrUtils().getHttpLog().clear();
	}

	/*
	 * Helper methods
	 */

	JsonObject doGet(String contextNodePath) throws Xdi2GraphException {

		String graphContextNodePath = this.graphContextNodePath(contextNodePath);

		// cache

		if (this.getCache() != null && ! graphContextNodePath.endsWith("/*")) {

			Element element = this.getCache().get(graphContextNodePath);
			JsonObject cachedJson = element == null ? null : (JsonObject) element.getObjectValue();
			if (cachedJson != null) return (JsonObject) jsonParser.parse(gson.toJson(cachedJson));
		}

		// http

		JsonObject json;

		try {

			json = this.getZephyrUtils().doGet(url(graphContextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP GET: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getCache() != null && ! graphContextNodePath.endsWith("/*")) {

			this.getCache().put(new Element(graphContextNodePath, json));
		}

		// done

		return json;
	}

	void doPut(String contextNodePath, JsonObject json) throws Xdi2GraphException {

		String graphContextNodePath = this.graphContextNodePath(contextNodePath);

		// http

		try {

			this.getZephyrUtils().doPut(this.url(graphContextNodePath), json);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getCache() != null && ! graphContextNodePath.endsWith("/*")) {

			Element element = this.getCache().get(graphContextNodePath);
			JsonObject cachedJson = element == null ? null : (JsonObject) element.getObjectValue();

			if (cachedJson != null) {

				for (Entry<String, JsonElement> entry : json.entrySet()) {

					cachedJson.add(entry.getKey(), entry.getValue());
				}

				this.getCache().put(new Element(graphContextNodePath, cachedJson));
			}
		}
	}

	void doPut(String contextNodePath, String key, JsonElement value) throws Xdi2GraphException {

		JsonObject json = new JsonObject();
		if (key != null) json.add(key, value);

		this.doPut(contextNodePath, json);
	}

	void doDelete(String contextNodePath) throws Xdi2GraphException {

		String graphContextNodePath = this.graphContextNodePath(contextNodePath);

		// http

		try {

			this.getZephyrUtils().doDelete(this.url(graphContextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP DELETE: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getCache() != null) {

			this.getCache().removeAll();

/* TODO			if (graphContextNodePath.endsWith("/*")) {

				this.getCache().remove(graphContextNodePath.substring(0, graphContextNodePath.length() - 2));
			} else {

				this.getCache().remove(graphContextNodePath);
			}*/
		}
	}

	String graphContextNodePath(String contextNodePath) {

		StringBuilder graphContextNodePath = new StringBuilder();

		if (this.getIdentifier() != null) graphContextNodePath.append("/" + ZephyrUtils.encode(this.getIdentifier()));
		graphContextNodePath.append(contextNodePath);

		return graphContextNodePath.toString();
	}

	String url(String graphContextNodePath) {

		String dataApi = this.getDataApi();
		if (dataApi.endsWith("/")) dataApi = dataApi.substring(0, dataApi.length() - 1);

		StringBuilder url = new StringBuilder();

		url.append(dataApi);
		url.append(graphContextNodePath);
		url.append("?token=" + this.getOauthToken());

		return url.toString();
	}

	/*
	 * Getters/Setters
	 */

	public String getIdentifier() {

		return this.identifier;
	}

	public String getDataApi() {

		return this.dataApi;
	}

	public String getOauthToken() {

		return this.oauthToken;
	}

	public Cache getCache() {

		return this.cache;
	}

	public ZephyrUtils getZephyrUtils() {

		return this.zephyrUtils;
	}
}
