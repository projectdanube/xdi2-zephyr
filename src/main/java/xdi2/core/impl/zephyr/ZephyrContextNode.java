package xdi2.core.impl.zephyr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;

	private static final Logger log = LoggerFactory.getLogger(ZephyrContextNode.class);

	private XDI3SubSegment arcXri;
	private JSONObject json;

	ZephyrContextNode(ZephyrGraph graph, ZephyrContextNode contextNode, XDI3SubSegment arcXri, JSONObject json) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.json = json;
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	private JSONObject getJson() {

		if (this.json != null) {

			// Zephyr request

			this.json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false));
		}

		return this.json;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, true);

		if (this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the context node " + arcXri + ".");

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(arcXri, false), null, null);

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, arcXri, null);
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, false);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(arcXri, false), null, null);

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, arcXri, null);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		// Zephyr request

		final JSONObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(true));

		// parse JSON

		Iterator<Entry<String, Object>> entries = json.entrySet().iterator();

		return new SelectingMappingIterator<Entry<String, Object>, ContextNode> (entries) {

			@Override
			public boolean select(Entry<String, Object> entry) {

				if (! entry.getKey().startsWith("/")) {

					log.warn("Invalid key in JSON object: " + entry.getKey());
					return false;
				}

				if (entry.getValue() instanceof JSONObject) {

					log.warn("Invalid value in JSON object: " + entry.getValue() + " (not a JSON object)");
					return false;
				}

				return true;
			}

			@Override
			public ContextNode map(Entry<String, Object> entry) {

				XDI3SubSegment arcXri = XDI3SubSegment.create(entry.getKey().substring(1));
				JSONObject innerJson = (JSONObject) entry.getValue();

				return new ZephyrContextNode((ZephyrGraph) ZephyrContextNode.this.getGraph(), ZephyrContextNode.this, arcXri, innerJson);
			}
		};
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doDelete(this.contextNodePath(arcXri, false));
	}

	@Override
	public void deleteContextNodes() {

		// TODO

		super.deleteContextNodes();
	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, true);

		JSONArray array = this.getJson().getJSONArray(arcXri.toString());
		if (array == null) array = new JSONArray();
		array.add(targetContextNode.getXri().toString());

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);

		// done

		return new ZephyrRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, false);

		JSONArray array = this.getJson().getJSONArray(arcXri.toString());
		if (array == null) array = new JSONArray();
		if (! array.contains(targetContextNode.getXri().toString())) array.add(targetContextNode.getXri().toString());

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);

		// done

		return new ZephyrRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		JSONArray array = this.getJson().getJSONArray(arcXri.toString());
		if (array == null) return null;
		if (! array.contains(targetContextNodeXri.toString())) return null;

		// done

		return new ZephyrRelation(this, arcXri, targetContextNodeXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDI3Segment arcXri) {

		JSONArray array = this.getJson().getJSONArray(arcXri.toString());
		if (array == null) return new EmptyIterator<Relation> ();

		// parse JSON

		return new SelectingMappingIterator<Object, Relation> (array.iterator()) {

			@Override
			public boolean select(Object object) {

				if (object instanceof JSONArray) {

					log.warn("Invalid element in JSON array: " + object + " (not a string)");
					return false;
				}

				return true;
			}

			@Override
			public Relation map(Object object) {

				XDI3Segment targetContextNodeXri = XDI3Segment.create((String) object);

				return new ZephyrRelation(ZephyrContextNode.this, arcXri, targetContextNodeXri);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		JSONObject json = this.getJson();

		// parse JSON

		Iterator<Entry<String, Object>> entries = json.entrySet().iterator();

		return new CompositeIterator<Relation> (new SelectingMappingIterator<Entry<String, Object>, Iterator<? extends Relation>> (entries) {

			@Override
			public boolean select(Entry<String, Object> entry) {

				if (XDIConstants.XRI_SS_LITERAL.toString().equals(entry.getKey())) {

					return false;
				}

				if (entry.getValue() instanceof JSONArray) {

					log.warn("Invalid value in JSON object: " + entry.getValue() + " (not a JSONArray)");
					return false;
				}

				return true;
			}

			@Override
			public Iterator<? extends Relation> map(Entry<String, Object> entry) {

				final XDI3Segment arcXri = XDI3Segment.create(entry.getKey());
				JSONArray innerJson = (JSONArray) entry.getValue();

				return new SelectingMappingIterator<Object, Relation> (innerJson.iterator()) {

					@Override
					public boolean select(Object object) {

						if (object instanceof JSONArray) {

							log.warn("Invalid element in JSON array: " + object + " (not a string)");
							return false;
						}

						return true;
					}

					@Override
					public Relation map(Object object) {

						XDI3Segment targetContextNodeXri = XDI3Segment.create((String) object);

						return new ZephyrRelation(ZephyrContextNode.this, arcXri, targetContextNodeXri);
					}
				};
			}
		});
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		JSONArray array = this.getJson().getJSONArray(arcXri.toString());
		if (array.contains(targetContextNodeXri.toString())) array.remove(targetContextNodeXri.toString());

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);
	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {

		JSONArray array = new JSONArray();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);
	}

	@Override
	public void deleteRelations() {

		JSONObject json = this.getJson();
		String value = json.getString(XDIConstants.XRI_S_LITERAL.toString());
		json = new JSONObject();
		if (value != null) json.put(XDIConstants.XRI_S_LITERAL.toString(), value);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), json);
	}

	@Override
	public Literal createLiteral(String literalData) {

		this.checkLiteral(literalData, true);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), XDIConstants.XRI_S_LITERAL.toString(), literalData);

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public Literal getLiteral() {

		String literalData = this.getJson().getString(XDIConstants.XRI_S_LITERAL.toString());
		if (literalData == null) return null;

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public void deleteLiteral() {

		JSONObject json = this.getJson();
		String value = json.getString(XDIConstants.XRI_S_LITERAL.toString());
		json = new JSONObject();
		if (value != null) json.put(XDIConstants.XRI_S_LITERAL.toString(), value);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), json);
	}

	String contextNodePath(XDI3SubSegment arcXri, boolean star) {

		StringBuilder contextNodePath = new StringBuilder();

		try {

			if (this.isRootContextNode()) {

			} else {

				contextNodePath.append(URLEncoder.encode(this.getArcXri().toString(), "UTF-8"));

				for (ContextNode contextNode = this.getContextNode(); 
						contextNode != null && ! contextNode.isRootContextNode(); 
						contextNode = contextNode.getContextNode()) {

					contextNodePath.insert(0, URLEncoder.encode(contextNode.getArcXri().toString(), "UTF-8") + "/");
				}
			}

			if (arcXri != null) {

				contextNodePath.append("/" + URLEncoder.encode(arcXri.toString(), "UTF-8"));
			}

			if (star) {

				contextNodePath.append("*");
			}
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		return contextNodePath.toString();
	}

	String contextNodePath(boolean star) {

		return contextNodePath(null, star);
	}
}
