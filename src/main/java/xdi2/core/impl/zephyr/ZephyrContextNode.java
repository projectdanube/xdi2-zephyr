package xdi2.core.impl.zephyr;

import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.impl.zephyr.util.ZephyrApi;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;

	private static final Logger log = LoggerFactory.getLogger(ZephyrContextNode.class);

	private XDI3SubSegment arcXri;

	ZephyrContextNode(ZephyrGraph graph, ZephyrContextNode contextNode, XDI3SubSegment arcXri) {

		super(graph, contextNode);

		this.arcXri = arcXri;
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	private ContextNode createContextNodeInternal(XDI3SubSegment arcXri) {

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(ZephyrContextNode.contextNodePath(this, arcXri, false), null, null);

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, arcXri);
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, true);

		return this.createContextNodeInternal(arcXri);
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, false);

		return this.createContextNodeInternal(arcXri);
	}

	@Override
	public ContextNode getContextNode(final XDI3SubSegment contextNodeArcXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, contextNodeArcXri, false));
		if (json == null) return null;

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, contextNodeArcXri);
	}

	@Override
	public ContextNode getDeepContextNode(XDI3Segment contextNodeArcXris) {

		if (XDIConstants.XRI_S_ROOT.equals(contextNodeArcXris) && this.isRootContextNode()) return this;

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, contextNodeArcXris, false));
		if (json == null) return null;

		// done

		ZephyrContextNode zephyrContextNode = this;

		for (XDI3SubSegment contextNodeArcXri : contextNodeArcXris.getSubSegments()) {

			zephyrContextNode = new ZephyrContextNode((ZephyrGraph) this.getGraph(), zephyrContextNode, contextNodeArcXri);
		}

		return zephyrContextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		// Zephyr request

		final JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, true));
		if (json == null) return new EmptyIterator<ContextNode> ();

		// parsing

		final String prefix = ((ZephyrGraph) this.getGraph()).graphContextNodePath(ZephyrContextNode.contextNodePath(this, false)) + "/";

		Iterator<Entry<String, JsonElement>> entries = json.entrySet().iterator();

		return new SelectingMappingIterator<Entry<String, JsonElement>, ContextNode> (entries) {

			@Override
			public boolean select(Entry<String, JsonElement> entry) {

				if (! entry.getKey().startsWith(prefix)) {

					return false;
				}

				if (entry.getKey().substring(prefix.length()).indexOf('/') != -1) {

					return false;
				}

				if (! (entry.getValue() instanceof JsonObject)) {

					log.warn("Invalid value in JSON object: " + entry.getValue() + " (not a JSON object)");
					return false;
				}

				return true;
			}

			@Override
			public ContextNode map(Entry<String, JsonElement> entry) {

				XDI3SubSegment arcXri = XDI3SubSegment.create(ZephyrApi.decode(entry.getKey().substring(prefix.length())));

				return new ZephyrContextNode((ZephyrGraph) ZephyrContextNode.this.getGraph(), ZephyrContextNode.this, arcXri);
			}
		};
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		// delete relations and incoming relations

		contextNode.deleteRelations();
		contextNode.deleteIncomingRelations();

		for (ContextNode innerContextNode : contextNode.getAllContextNodes()) {

			innerContextNode.deleteRelations();
			innerContextNode.deleteIncomingRelations();
		}

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doDelete(ZephyrContextNode.contextNodePath(this, arcXri, true));
	}

	@Override
	public void deleteContextNodes() {

		// TODO

		super.deleteContextNodes();
	}

	private Relation createRelationInternal(XDI3Segment arcXri, ContextNode targetContextNode) {

		String contextNodePath = ZephyrContextNode.contextNodePath(this, false);
		String targetContextNodePath = ZephyrContextNode.contextNodePath(targetContextNode, false);

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(contextNodePath);
		if (json == null) json = new JsonObject();

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) array = new JsonArray();
		JsonPrimitive jsonPrimitive = new JsonPrimitive(targetContextNode.getXri().toString());
		if (! new IteratorContains<JsonElement> (array.iterator(), jsonPrimitive).contains()) array.add(jsonPrimitive);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(contextNodePath, arcXri.toString(), array);

		// Zephyr request

		json = ((ZephyrGraph) this.getGraph()).doGet(targetContextNodePath);
		if (json == null) json = new JsonObject();

		// manipulation

		array = (JsonArray) json.get("/" + arcXri.toString());
		if (array == null) array = new JsonArray();
		jsonPrimitive = new JsonPrimitive(this.getXri().toString());
		if (! new IteratorContains<JsonElement> (array.iterator(), jsonPrimitive).contains()) array.add(jsonPrimitive);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(targetContextNodePath, "/" + arcXri.toString(), array);

		// done

		return new ZephyrRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, true);

		return this.createRelationInternal(arcXri, targetContextNode);
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, false);

		return this.createRelationInternal(arcXri, targetContextNode);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, false));
		if (json == null) json = new JsonObject();

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) return null;
		Iterator<JsonElement> iterator = array.iterator();
		if (! new IteratorContains<JsonElement> (iterator, new JsonPrimitive(targetContextNodeXri.toString())).contains()) return null;

		// done

		return new ZephyrRelation(this, arcXri, targetContextNodeXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDI3Segment arcXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, false));
		if (json == null) json = new JsonObject();

		// parsing

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) return new EmptyIterator<Relation> ();

		return new SelectingMappingIterator<JsonElement, Relation> (array.iterator()) {

			@Override
			public boolean select(JsonElement element) {

				if (! (element instanceof JsonPrimitive) || ! ((JsonPrimitive) element).isString()) {

					log.warn("Invalid element in JSON array: " + element + " (not a string)");
					return false;
				}

				return true;
			}

			@Override
			public Relation map(JsonElement element) {

				XDI3Segment targetContextNodeXri = XDI3Segment.create(((JsonPrimitive) element).getAsString());

				return new ZephyrRelation(ZephyrContextNode.this, arcXri, targetContextNodeXri);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, false));
		if (json == null) json = new JsonObject();

		// parsing

		Iterator<Entry<String, JsonElement>> entries = json.entrySet().iterator();

		return new CompositeIterator<Relation> (new SelectingMappingIterator<Entry<String, JsonElement>, Iterator<? extends Relation>> (entries) {

			@Override
			public boolean select(Entry<String, JsonElement> entry) {

				if (XDIConstants.XRI_SS_LITERAL.toString().equals(entry.getKey())) {

					return false;
				}

				if (entry.getKey().startsWith("/")) {

					return false;
				}

				if (! (entry.getValue() instanceof JsonArray)) {

					log.warn("Invalid value in JSON object: " + entry.getValue() + " (not a JSON array)");
					return false;
				}

				return true;
			}

			@Override
			public Iterator<? extends Relation> map(Entry<String, JsonElement> entry) {

				final XDI3Segment arcXri = XDI3Segment.create(entry.getKey());
				JsonArray innerJson = (JsonArray) entry.getValue();

				return new SelectingMappingIterator<JsonElement, Relation> (innerJson.iterator()) {

					@Override
					public boolean select(JsonElement element) {

						if (! (element instanceof JsonPrimitive) || ! ((JsonPrimitive) element).isString()) {

							log.warn("Invalid element in JSON array: " + element + " (not a string)");
							return false;
						}

						return true;
					}

					@Override
					public Relation map(JsonElement element) {

						XDI3Segment targetContextNodeXri = XDI3Segment.create(((JsonPrimitive) element).getAsString());

						return new ZephyrRelation(ZephyrContextNode.this, arcXri, targetContextNodeXri);
					}
				};
			}
		});
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, false));
		if (json == null) json = new JsonObject();

		// parsing

		Iterator<Entry<String, JsonElement>> entries = json.entrySet().iterator();

		return new CompositeIterator<Relation> (new SelectingMappingIterator<Entry<String, JsonElement>, Iterator<? extends Relation>> (entries) {

			@Override
			public boolean select(Entry<String, JsonElement> entry) {

				if (XDIConstants.XRI_SS_LITERAL.toString().equals(entry.getKey())) {

					return false;
				}

				if (! entry.getKey().startsWith("/")) {

					return false;
				}

				if (! (entry.getValue() instanceof JsonArray)) {

					log.warn("Invalid value in JSON object: " + entry.getValue() + " (not a JSON array)");
					return false;
				}

				return true;
			}

			@Override
			public Iterator<? extends Relation> map(Entry<String, JsonElement> entry) {

				final XDI3Segment arcXri = XDI3Segment.create(entry.getKey().substring(1));
				JsonArray innerJson = (JsonArray) entry.getValue();

				return new SelectingMappingIterator<JsonElement, Relation> (innerJson.iterator()) {

					@Override
					public boolean select(JsonElement element) {

						if (! (element instanceof JsonPrimitive) || ! ((JsonPrimitive) element).isString()) {

							log.warn("Invalid element in JSON array: " + element + " (not a string)");
							return false;
						}

						return true;
					}

					@Override
					public Relation map(JsonElement element) {

						XDI3Segment contextNodeXri = XDI3Segment.create(((JsonPrimitive) element).getAsString());
						ZephyrContextNode contextNode = (ZephyrContextNode) ZephyrContextNode.this.getGraph().getDeepContextNode(contextNodeXri);

						return new ZephyrRelation(contextNode, arcXri, ZephyrContextNode.this.getXri());
					}
				};
			}
		});
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		String contextNodePath = ZephyrContextNode.contextNodePath(this, false);
		String targetContextNodePath = ZephyrContextNode.contextNodePath(targetContextNodeXri, false);

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(contextNodePath); 
		if (json == null) json = new JsonObject();

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) array = new JsonArray();
		JsonPrimitive jsonPrimitive = new JsonPrimitive(targetContextNodeXri.toString());
		Iterator<JsonElement> iterator = array.iterator();
		if (! new IteratorContains<JsonElement> (iterator, jsonPrimitive).contains()) return;
		iterator.remove();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(contextNodePath, arcXri.toString(), array);

		// Zephyr request

		json = ((ZephyrGraph) this.getGraph()).doGet(targetContextNodePath); 
		if (json == null) json = new JsonObject();

		// manipulation

		array = (JsonArray) json.get("/" + arcXri.toString());
		if (array == null) array = new JsonArray();
		jsonPrimitive = new JsonPrimitive(this.getXri().toString());
		iterator = array.iterator();
		if (! new IteratorContains<JsonElement> (iterator, jsonPrimitive).contains()) return;
		iterator.remove();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(targetContextNodePath, "/" + arcXri.toString(), array);
	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {

		// TODO

		super.deleteRelations(arcXri);

		//		JsonArray array = new JsonArray();

		// Zephyr request

		//		((ZephyrGraph) this.getGraph()).doPut(ZephyrContextNode.contextNodePath(this, false), arcXri.toString(), array);
	}

	@Override
	public void deleteRelations() {

		// TODO, HTTP REPLACE would be useful

		super.deleteRelations();
	}

	@Override
	public void deleteIncomingRelations() {

		// TODO

		super.deleteIncomingRelations();
	}

	private Literal createLiteralInternal(Object literalData) {

		JsonArray array = new JsonArray();
		array.add(AbstractLiteral.literalDataToJsonPrimitive(literalData));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(ZephyrContextNode.contextNodePath(this, false), XDIConstants.XRI_S_LITERAL.toString(), array);

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public Literal createLiteral(Object literalData) {

		this.checkLiteral(literalData, true);

		return this.createLiteralInternal(literalData);
	}

	@Override
	public Literal setLiteral(Object literalData) {

		this.checkLiteral(literalData, false);

		return this.createLiteralInternal(literalData);
	}

	@Override
	public Literal getLiteral() {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(ZephyrContextNode.contextNodePath(this, false)); 
		if (json == null) json = new JsonObject();

		// manipulation

		JsonArray array = (JsonArray) json.get(XDIConstants.XRI_S_LITERAL.toString());
		if (array == null || array.size() < 1 || ! (array.get(0) instanceof JsonPrimitive) || ! ((JsonPrimitive) array.get(0)).isString()) return null;
		String literalData = ((JsonPrimitive) array.get(0)).getAsString();

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public void deleteLiteral() {

		JsonArray array = new JsonArray();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(ZephyrContextNode.contextNodePath(this, false), XDIConstants.XRI_S_LITERAL.toString(), array);
	}

	public static String contextNodePath(XDI3Segment contextNodeArcXris, XDI3Segment innerContextNodeArcXris, boolean star) {

		StringBuilder contextNodePath = new StringBuilder();

		if (contextNodeArcXris == null || XDIConstants.XRI_S_ROOT.equals(contextNodeArcXris)) {

		} else {

			for (XDI3SubSegment contextNodeArcXri : contextNodeArcXris.getSubSegments()) {

				contextNodePath.append("/" + ZephyrApi.encode(contextNodeArcXri.toString()));
			}
		}

		if (innerContextNodeArcXris != null) {

			for (XDI3SubSegment innerContextNodeArcXri : innerContextNodeArcXris.getSubSegments()) {

				contextNodePath.append("/" + ZephyrApi.encode(innerContextNodeArcXri.toString()));
			}
		}

		if (star) {

			contextNodePath.append("/*");
		}

		return contextNodePath.toString();
	}

	public static String contextNodePath(XDI3Segment contextNodeArcXris, XDI3SubSegment innerContextNodeArcXri, boolean star) {

		return contextNodePath(contextNodeArcXris, XDI3Segment.create(innerContextNodeArcXri), star);
	}

	public static String contextNodePath(ContextNode contextNode, XDI3Segment innerContextNodeArcXris, boolean star) {

		return contextNodePath(contextNode.getXri(), innerContextNodeArcXris, star);
	}

	public static String contextNodePath(ContextNode contextNode, XDI3SubSegment innerContextNodeArcXri, boolean star) {

		return contextNodePath(contextNode.getXri(), innerContextNodeArcXri, star);
	}

	public static String contextNodePath(XDI3Segment contextNodeArcXris, boolean star) {

		return contextNodePath(contextNodeArcXris, (XDI3Segment) null, star);
	}

	public static String contextNodePath(ContextNode contextNode, boolean star) {

		return contextNodePath(contextNode.getXri(), star);
	}
}
