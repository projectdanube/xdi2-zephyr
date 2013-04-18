package xdi2.core.impl.zephyr;

import java.io.IOException;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates graphs in zephyr.
 * 
 * @author markus
 */
public class ZephyrGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_DATA_API = "http://107.21.179.68:10002/";
	public static final String DEFAULT_OAUTH_TOKEN = "SECRET";

	private String dataApi;
	private String oauthToken;

	public ZephyrGraphFactory() {

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		// create cache

		Ehcache cache = CacheManager.getInstance().addCacheIfAbsent(identifier);

		// create graph

		ZephyrGraph graph = new ZephyrGraph(this, identifier, cache);

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
}
