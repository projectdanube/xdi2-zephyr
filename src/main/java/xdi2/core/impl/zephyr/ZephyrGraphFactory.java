package xdi2.core.impl.zephyr;

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
	public static final String DEFAULT_OAUTH_TOKEN = "?token=SECRET";
	public static  String rootNode;
	
	private String dataApi;
	private String oauthToken;
	

	public ZephyrGraphFactory() { 

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
	}
	
		
	public Graph openGraph(String identifier)
	{
		try {
		this.rootNode = identifier;
		String jsonObject = ZephyrUtils.doGet(DEFAULT_DATA_API + identifier + "/*" + DEFAULT_OAUTH_TOKEN);
		
		return new ZephyrGraph(this);
		
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
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
