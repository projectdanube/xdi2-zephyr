package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.util.UUID;

import net.sf.ehcache.CacheManager;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;
import xdi2.core.impl.zephyr.util.ZephyrApi;
import xdi2.core.impl.zephyr.util.ZephyrCache;

/**
 * GraphFactory that creates graphs in Zephyr.
 * 
 * @author markus
 */
public class ZephyrGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_DATA_API = "http://127.0.0.1:10002/";
	public static final String DEFAULT_OAUTH_TOKEN = "SECRET";
	public static final ZephyrCache DEFAULT_ZEPHYR_CACHE;
	public static final ZephyrApi DEFAULT_ZEPHYR_API;

	static {

		DEFAULT_ZEPHYR_CACHE = new ZephyrCache(CacheManager.create(ZephyrGraphFactory.class.getResourceAsStream("ehcache.xml")).getCache("ZephyrGraphFactory_DEFAULT_CACHE"));
		DEFAULT_ZEPHYR_API = new ZephyrApi();
	}

	private String dataApi;
	private String oauthToken;
	private ZephyrCache zephyrCache; 
	private ZephyrApi zephyrApi;

	public ZephyrGraphFactory() {

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
		this.zephyrCache = DEFAULT_ZEPHYR_CACHE;
		this.zephyrApi = DEFAULT_ZEPHYR_API;
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		// no identifier? create UUID

		if (identifier == null) identifier = UUID.randomUUID().toString();

		// create graph

		ZephyrGraph graph = new ZephyrGraph(this, identifier, this.getDataApi(), this.getOauthToken(), this.getZephyrCache(), this.getZephyrApi());

		// Zephyr request

		graph.doPut("", null, null);

		// done

		return graph;
	}

	public String getDataApi() {

		return this.dataApi;
	}

	public void setDataApi(String dataApi) {

		this.dataApi = dataApi;
	}

	public String getOauthToken() {

		return this.oauthToken;
	}

	public void setOauthToken(String oauthToken) {

		this.oauthToken = oauthToken;
	}

	public ZephyrCache getZephyrCache() {

		return this.zephyrCache;
	}

	public void setZephyrCache(ZephyrCache zephyrCache) {

		this.zephyrCache = zephyrCache;
	}

	public ZephyrApi getZephyrApi() {

		return this.zephyrApi;
	}

	public void setZephyrUtils(ZephyrApi zephyrApi) {

		this.zephyrApi = zephyrApi;
	}
}
