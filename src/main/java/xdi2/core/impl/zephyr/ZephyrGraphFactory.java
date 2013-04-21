package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;
import xdi2.core.impl.zephyr.util.ZephyrUtils;

/**
 * GraphFactory that creates graphs in zephyr.
 * 
 * @author markus
 */
public class ZephyrGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_DATA_API = "http://107.21.179.68:10002/";
	public static final String DEFAULT_OAUTH_TOKEN = "SECRET";
	public static final Cache DEFAULT_CACHE;
	public static final ZephyrUtils DEFAULT_ZEPHYR_UTILS;

	static {

		DEFAULT_CACHE = CacheManager.create(ZephyrGraphFactory.class.getResourceAsStream("ehcache.xml")).getCache("ZephyrGraphFactory_DEFAULT_CACHE");
		DEFAULT_ZEPHYR_UTILS = new ZephyrUtils();
	}

	private String dataApi;
	private String oauthToken;
	private Cache cache; 
	private ZephyrUtils zephyrUtils;

	public ZephyrGraphFactory() {

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
		this.cache = DEFAULT_CACHE;
		this.zephyrUtils = DEFAULT_ZEPHYR_UTILS;
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		// no identifier? create UUID

		if (identifier == null) identifier = UUID.randomUUID().toString();

		// create graph

		ZephyrGraph graph = new ZephyrGraph(this, identifier, this.getDataApi(), this.getOauthToken(), this.getCache(), this.getZephyrUtils());

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

	public Cache getCache() {

		return this.cache;
	}

	public void setCache(Cache cache) {

		this.cache = cache;
	}

	public ZephyrUtils getZephyrUtils() {

		return this.zephyrUtils;
	}

	public void setZephyrUtils(ZephyrUtils zephyrUtils) {

		this.zephyrUtils = zephyrUtils;
	}
}
