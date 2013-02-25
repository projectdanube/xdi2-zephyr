package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	public static final String DEFAULT_OAUTH_TOKEN = "/?token=SECRET";
	private Map<String, ZephyrGraph> graphs;


	private static ZephyrGraphFactory instance = null;

	private int sortmode;
	
	private String dataApi;
	private String oauthToken;
	private String rootNode;

	public ZephyrGraphFactory() { 

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
		
		this.graphs = new HashMap<String, ZephyrGraph> ();
	}
	
	public static ZephyrGraphFactory getInstance() {

		if (instance == null) instance = new ZephyrGraphFactory();

		return instance;
	}
	
	
	public Graph getGraph(String identifier)
	{
		Graph graph = null;
		try {
		this.setRootNode(identifier);
		String jsonObject;
		
		jsonObject = ZephyrUtils.doGet(DEFAULT_DATA_API + identifier + "/*" + DEFAULT_OAUTH_TOKEN);
		
		if(jsonObject.equals("{}"))
		{
			graph = this.parseGraph(jsonObject);
		}
		else
		{
			String strGraph = ZephyrUtils.mapGraph(jsonObject);
			graph = (new ZephyrGraphFactory()).parseGraph(strGraph);
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return graph;
	}

	@Override
	public ZephyrGraph openGraph(String identifier) throws IOException {
		
      ZephyrGraph graph = this.graphs.get(identifier);
		
		if (graph == null) {
			
			graph = (ZephyrGraph) this.openGraph();
			this.graphs.put(identifier, graph);
		}
		
		return graph;
		//return new ZephyrGraph(this);
		
	}
	
	@Override
	public ZephyrGraph openGraph() {

		// create new graph

		return new ZephyrGraph(this, this.sortmode);
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
	
	public String getRootNode() {
		return rootNode;
	}

	public void setRootNode(String rootNode) {
		this.rootNode = rootNode;
	}

}
