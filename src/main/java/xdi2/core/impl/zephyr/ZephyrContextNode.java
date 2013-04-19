package xdi2.core.impl.zephyr;

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

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, true);

		if (this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the context node " + arcXri + ".");

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(arcXri, false), null, null);

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, arcXri);
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, false);

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(arcXri, false), null, null);

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, arcXri);
	}

	@Override
	public ContextNode getContextNode(final XDI3SubSegment contextNodeArcXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(contextNodeArcXri, false));
		if (json == null) return null;

		// done

		return new ZephyrContextNode((ZephyrGraph) this.getGraph(), this, contextNodeArcXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		// Zephyr request

		final JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(true));
		if (json == null) return new EmptyIterator<ContextNode> ();

		// parsing

		final String prefix = ((ZephyrGraph) this.getGraph()).graphContextNodePath(this.contextNodePath(false)) + "/";

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

				XDI3SubSegment arcXri = XDI3SubSegment.create(ZephyrUtils.decode(entry.getKey().substring(prefix.length())));

				return new ZephyrContextNode((ZephyrGraph) ZephyrContextNode.this.getGraph(), ZephyrContextNode.this, arcXri);
			}
		};
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {

		// delete incoming relations

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		for (Iterator<Relation> relations = contextNode.getIncomingRelations(); relations.hasNext(); ) relations.next().delete();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doDelete(this.contextNodePath(arcXri, true));
	}

	@Override
	public void deleteContextNodes() {

		// TODO

		super.deleteContextNodes();
	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, true);

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) array = new JsonArray();
		array.add(new JsonPrimitive(targetContextNode.getXri().toString()));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);

		// done

		return new ZephyrRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, false);

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		if (array == null) array = new JsonArray();
		Iterator<JsonElement> iterator = array.iterator();
		if (! new IteratorContains<JsonElement> (iterator, new JsonPrimitive(targetContextNode.getXri().toString())).contains()) array.add(new JsonPrimitive(targetContextNode.getXri().toString()));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);

		// done

		return new ZephyrRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

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

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

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

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

		// parsing

		Iterator<Entry<String, JsonElement>> entries = json.entrySet().iterator();

		return new CompositeIterator<Relation> (new SelectingMappingIterator<Entry<String, JsonElement>, Iterator<? extends Relation>> (entries) {

			@Override
			public boolean select(Entry<String, JsonElement> entry) {

				if (XDIConstants.XRI_SS_LITERAL.toString().equals(entry.getKey())) {

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
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

		// manipulation

		JsonArray array = (JsonArray) json.get(arcXri.toString());
		Iterator<JsonElement> iterator = array.iterator();
		if (! new IteratorContains<JsonElement> (iterator, new JsonPrimitive(targetContextNodeXri.toString())).contains()) return;
		iterator.remove();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);
	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {

		JsonArray array = new JsonArray();

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), arcXri.toString(), array);
	}

	@Override
	public void deleteRelations() {

		// TODO

		super.deleteRelations();
	}

	@Override
	public Literal createLiteral(String literalData) {

		this.checkLiteral(literalData, true);

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(literalData));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), XDIConstants.XRI_S_LITERAL.toString(), array);

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public Literal setLiteral(String literalData) {

		this.checkLiteral(literalData, false);

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(literalData));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), XDIConstants.XRI_S_LITERAL.toString(), array);

		// done

		return new ZephyrLiteral(this, literalData);
	}

	@Override
	public Literal getLiteral() {

		// Zephyr request

		JsonObject json = ((ZephyrGraph) this.getGraph()).doGet(this.contextNodePath(false)); 

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

		((ZephyrGraph) this.getGraph()).doPut(this.contextNodePath(false), XDIConstants.XRI_S_LITERAL.toString(), array);
	}

	String contextNodePath(XDI3SubSegment arcXri, boolean star) {

		StringBuilder contextNodePath = new StringBuilder();

		if (this.isRootContextNode()) {

		} else {

			contextNodePath.append("/" + ZephyrUtils.encode(this.getArcXri().toString()));

			for (ContextNode contextNode = this.getContextNode(); 
					contextNode != null && ! contextNode.isRootContextNode(); 
					contextNode = contextNode.getContextNode()) {

				contextNodePath.insert(0, "/" + ZephyrUtils.encode(contextNode.getArcXri().toString()));
			}
		}

		if (arcXri != null) {

			contextNodePath.append("/" + ZephyrUtils.encode(arcXri.toString()));
		}

		if (star) {

			contextNodePath.append("/*");
		}

		return contextNodePath.toString();
	}

	String contextNodePath(boolean star) {

		return contextNodePath(null, star);
	}
}
