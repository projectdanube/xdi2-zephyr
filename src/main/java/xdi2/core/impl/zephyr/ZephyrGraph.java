package xdi2.core.impl.zephyr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractGraph;

import com.alibaba.fastjson.JSONObject;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;

	private String identifier;

	private ZephyrContextNode rootContextNode;

	ZephyrGraph(ZephyrGraphFactory graphFactory, String identifier) {

		super(graphFactory);

		this.identifier = identifier;

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

	JSONObject doGet(String contextNodePath) throws Xdi2GraphException {

		try {

			return ZephyrUtils.doGet(url(contextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP GET: " + ex.getMessage(), ex);
		}
	}

	void doPut(String contextNodePath, JSONObject json) throws Xdi2GraphException {

		try {

			ZephyrUtils.doPut(url(contextNodePath), json);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}
	}

	void doPut(String contextNodePath, String key, Object value) throws Xdi2GraphException {

		try {

			ZephyrUtils.doPut(url(contextNodePath), key, value);
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP PUT: " + ex.getMessage(), ex);
		}
	}

	void doDelete(String contextNodePath) throws Xdi2GraphException {

		try {

			ZephyrUtils.doDelete(url(contextNodePath));
		} catch (IOException ex) {

			throw new Xdi2GraphException("Problem with HTTP DELETE: " + ex.getMessage(), ex);
		}
	}

	String url(String contextNodePath) {

		try {

			ZephyrGraphFactory graphFactory = (ZephyrGraphFactory) this.getGraphFactory();

			StringBuilder url = new StringBuilder();

			url.append(graphFactory.getDataApi());
			if (! graphFactory.getDataApi().endsWith("/")) url.append("/");
			if (this.getIdentifier() != null) url.append(URLEncoder.encode(this.getIdentifier(), "UTF-8") + "/");
			url.append(contextNodePath);
			url.append("?token=" + graphFactory.getOauthToken());

			return url.toString();
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
