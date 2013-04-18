package xdi2.core.impl.zephyr;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.exceptions.Xdi2GraphException;
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

	private Map<String, ZephyrGraph> graphs;

	public ZephyrGraphFactory() {

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
		this.graphs = new HashMap<String, ZephyrGraph> ();
	}

	@Override
	public Graph openGraph(String identifier) {

		try {

			ZephyrGraph graph = this.graphs.get(identifier);

			if (graph == null) {

				graph = new ZephyrGraph(this,identifier);
				this.graphs.put(identifier, graph);
				ZephyrUtils.doPut(getDataApi() + "/" + identifier + "?token=" + getOauthToken(), "", "");
			}

			return graph;
		} catch (Exception e) {

			throw new Xdi2GraphException(e.getMessage(), e);
		}
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
