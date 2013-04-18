package xdi2.core.impl.zephyr;

import java.io.IOException;

import net.sf.ehcache.Ehcache;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractGraph;

import com.alibaba.fastjson.JSONObject;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;

	private String identifier;
	private Ehcache cache;

	private ZephyrContextNode rootContextNode;

	ZephyrGraph(ZephyrGraphFactory graphFactory, String identifier, Ehcache cache) {

		super(graphFactory);

		this.identifier = identifier;
		this.cache = cache;

		this.rootContextNode = new ZephyrContextNode(this, null, null, null);
	}

	@Override
	public ContextNode getRootContextNode() {

		return this.rootContextNode;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public String getIdentifier() {

		return this.identifier;
	}

	public Ehcache getEhcache() {

		return this.cache;
	}

	JSONObject doGet(String contextNodePath) throws Xdi2GraphException {

		try {

			return ZephyrUtils.doGet(url(graphContextNodePath(contextNodePath)));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP GET: " + ex.getMessage(), ex);
		}
	}

	void doPut(String contextNodePath, JSONObject json) throws Xdi2GraphException {

		try {

			ZephyrUtils.doPut(url(graphContextNodePath(contextNodePath)), json);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}
	}

	void doPut(String contextNodePath, String key, Object value) throws Xdi2GraphException {

		try {

			ZephyrUtils.doPut(url(graphContextNodePath(contextNodePath)), key, value);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}
	}

	void doDelete(String contextNodePath) throws Xdi2GraphException {

		try {

			ZephyrUtils.doDelete(url(graphContextNodePath(contextNodePath)));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP DELETE: " + ex.getMessage(), ex);
		}
	}

	String graphContextNodePath(String contextNodePath) {

		StringBuilder graphContextNodePath = new StringBuilder();

		if (this.getIdentifier() != null) graphContextNodePath.append("/" + ZephyrUtils.encode(this.getIdentifier()));
		graphContextNodePath.append(contextNodePath);

		return graphContextNodePath.toString();
	}

	String url(String graphContextNodePath) {

		ZephyrGraphFactory graphFactory = (ZephyrGraphFactory) this.getGraphFactory();
		String dataApi = graphFactory.getDataApi();
		if (dataApi.endsWith("/")) dataApi = dataApi.substring(0, dataApi.length() - 1);

		StringBuilder url = new StringBuilder();

		url.append(dataApi);
		url.append(graphContextNodePath);
		url.append("?token=" + graphFactory.getOauthToken());

		return url.toString();
	}
}
