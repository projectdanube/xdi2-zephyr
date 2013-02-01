package xdi2.core.impl.cloudfabric;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates graphs in CloudFabric.
 * 
 * @author markus
 */
public class CloudFabricGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_DATA_API = "http://127.0.0.1:10002/";
	public static final String DEFAULT_OAUTH_TOKEN = null;

	private String dataApi;
	private String oauthToken;

	public CloudFabricGraphFactory() { 

		this.dataApi = DEFAULT_DATA_API;
		this.oauthToken = DEFAULT_OAUTH_TOKEN;
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {
		// TODO Auto-generated method stub
		return new CloudFabricGraph(this);
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
