package xdi2.core.impl.zephyr;

import java.io.IOException;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.impl.zephyr.util.ZephyrApi;
import xdi2.core.impl.zephyr.util.ZephyrCache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;

	private String identifier;

	private String dataApi;
	private String oauthToken;
	private ZephyrCache zephyrCache;
	private ZephyrApi zephyrApi;

	private ZephyrContextNode rootContextNode;

	ZephyrGraph(ZephyrGraphFactory graphFactory, String identifier, String dataApi, String oauthToken, ZephyrCache zephyrCache, ZephyrApi zephyrApi) {

		super(graphFactory);

		this.identifier = identifier;

		this.dataApi = dataApi;
		this.oauthToken = oauthToken;
		this.zephyrCache = zephyrCache;
		this.zephyrApi = zephyrApi;

		this.rootContextNode = new ZephyrContextNode(this, null, null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.rootContextNode;
	}

	@Override
	public void close() {

		if (this.getZephyrCache() != null) this.getZephyrCache().removeAll();
		if (this.getZephyrApi() != null) this.getZephyrApi().getZephyrApiLog().clear();
	}

	/*
	 * Helper methods
	 */

	JsonObject doGet(String contextNodePath) throws Xdi2GraphException {

		String graphContextNodePath = this.graphContextNodePath(contextNodePath);

		// cache

		JsonObject json;

		if (this.getZephyrCache() != null) {

			json = this.getZephyrCache().fetchFromCache(graphContextNodePath);
			if (json != null) return json;
		}

		// http

		try {

			json = this.getZephyrApi().doGet(url(graphContextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP GET: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getZephyrCache() != null) {

			this.getZephyrCache().storeIntoCache(graphContextNodePath, json);
		}

		// done

		return json;
	}

	void doPut(String contextNodePath, JsonObject json) throws Xdi2GraphException {

		String graphContextNodePath = this.graphContextNodePath(contextNodePath);

		if (graphContextNodePath.endsWith("/*")) throw new IllegalArgumentException("Invalid graph context node path for PUT: " + graphContextNodePath);

		// http

		try {

			this.getZephyrApi().doPut(this.url(graphContextNodePath), json);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getZephyrCache() != null) {

			this.getZephyrCache().mergeIntoCache(graphContextNodePath, json);
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

			this.getZephyrApi().doDelete(this.url(graphContextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP DELETE: " + ex.getMessage(), ex);
		}

		// cache

		if (this.getZephyrCache() != null) {

			this.getZephyrCache().removeAll();
		}
	}

	String graphContextNodePath(String contextNodePath) {

		StringBuilder graphContextNodePath = new StringBuilder();

		if (this.getIdentifier() != null) graphContextNodePath.append("/" + ZephyrApi.encode(this.getIdentifier()));
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

	public ZephyrCache getZephyrCache() {

		return this.zephyrCache;
	}

	public ZephyrApi getZephyrApi() {

		return this.zephyrApi;
	}
}
