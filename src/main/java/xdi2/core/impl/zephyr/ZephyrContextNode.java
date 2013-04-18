package xdi2.core.impl.zephyr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.memory.MemoryContextNode;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;
	private static final Logger log = LoggerFactory.getLogger(ZephyrContextNode.class);
	private Map<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> relations;
	private Map<XDI3SubSegment, ZephyrContextNode> contextNodes;
	private List<String> lstArcXRIs = null;
	XDI3SubSegment arcXri;

	// ContextNode objParentNode;

	ZephyrContextNode(Graph graph, ContextNode contextNode) {
		super(graph, contextNode);
		this.relations = new HashMap<XDI3Segment, Map<XDI3Segment, ZephyrRelation>>();
		this.contextNodes = new HashMap<XDI3SubSegment, ZephyrContextNode>();
	}

	@Override
	public XDI3SubSegment getArcXri() {
		return this.arcXri;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {
		try {
			// URI uri = URIUtils.createURI(PROTOCOL, URL, PORT, MailerSDKConstants.ADD_BRANDS_SERVICE_URL, null, null)
			String contextnodes = null;
			String response = null;
			Iterator<Entry<XDI3SubSegment, ZephyrContextNode>> it = this.contextNodes.entrySet().iterator();
			if (this.contextNodes.size() != 0) {
				while (it.hasNext()) {
					Entry<XDI3SubSegment, ZephyrContextNode> entry = it.next();
					String Key = entry.getKey().toString();
					if (Key.equals(arcXri.toString())) {
						throw new Xdi2GraphException("Context Node already exists");
					} else {
						contextnodes = Key + ",";
					}
				}
				contextnodes = "[" + contextnodes + arcXri.toString() + "]";
				contextnodes = StringUtils.removeStart(contextnodes, ",");

			} else {
				String[] arrnodes = null;
				if (this.arcXri == null)
					response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
				else
					response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

				log.info(response);
				JSONObject jsonGraph = new JSONObject(response);
				Iterator<String> nodes = jsonGraph.keys();
				while (nodes.hasNext()) {
					String key = nodes.next();
					if (key.equals("()")) {
						contextnodes = jsonGraph.getString(key).replace("[", "").replace("]", "");
					}
				}
				if (contextnodes != null && contextnodes.split(",") != null) {
					arrnodes = contextnodes.split(",");
					for (String node : arrnodes) {
						if (node.equals(arcXri.toString())) {
							throw new Xdi2GraphException("Context Node already exists");
						}
					}
					contextnodes = "[" + contextnodes + "," + arcXri.toString() + "]";
				} else {
					contextnodes = "[" + arcXri.toString() + "]";
				}
			}

			if (this.arcXri != null) {

				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "/" + arcXri.toString() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "", "");
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "()", contextnodes);
			} else {
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + arcXri.toString() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "", "");
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "()", contextnodes);
				// this.objParentNode = null; // For Root Context Node, parent object will be null
			}

			ZephyrContextNode contextNode = new ZephyrContextNode(this.getGraph(), this);
			contextNode.arcXri = arcXri;
			// contextNode.objParentNode = this;
			this.contextNodes.put(arcXri, contextNode);
			return contextNode;

		} catch (Xdi2GraphException e) {
			throw e;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		try {
			if (this.contextNodes.size() != 0) {
				return new ReadOnlyIterator<ContextNode>(new CastingIterator<ZephyrContextNode, ContextNode>(this.contextNodes.values().iterator()));
			} else {
				String response = "";
				String[] arrnodes = null;
				String contextnodes = null;
				response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

				log.info(response);
				JSONObject jsonGraph = new JSONObject(response);

				ZephyrContextNode contextNode = null;
				List<ContextNode> lstContextNode = new ArrayList<ContextNode>();

				Iterator<String> nodes = jsonGraph.keys();
				while (nodes.hasNext()) {
					String key = nodes.next();
					if (key.equals("()")) {
						contextnodes = jsonGraph.getString(key).replace("[", "").replace("]", "");
						if (contextnodes != null && contextnodes.split(",") != null) {
							arrnodes = contextnodes.split(",");
							for (String node : arrnodes) {
								contextNode = new ZephyrContextNode(this.getGraph(), this);
								contextNode.arcXri = XDI3SubSegment.create(node);
								lstContextNode.add(contextNode);
							}

						}
					}

				}
				ReadOnlyIterator<ContextNode> itrContextNodes = new ReadOnlyIterator<ContextNode>(lstContextNode.iterator());

				return itrContextNodes;

			}
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		try {
			// this.objParentNode = this.getContextNode();
			
			String response = "";
			
			String contextnodes = null;
			response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

			log.info(response);
			JSONObject jsonGraph = new JSONObject(response);

			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (key.equals("()")) {
					contextnodes = jsonGraph.getString(key).replace("[", "").replace("]", "");
					contextnodes = contextnodes.replace(arcXri.toString() + ",", "").replace(arcXri.toString(), "");
					contextnodes = StringUtils.removeEnd(contextnodes, ",");
					break;
				}

			}
			
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "()", contextnodes);
			
			// Delete URL and content of the contextnode.
			ZephyrUtils.doDelete(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "/" + arcXri.toString() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			
			this.contextNodes.remove(XDI3SubSegment.create(arcXri.toString()));

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteContextNodes() {
		try {
			// this.objParentNode = this.getContextNode();

			Iterator<Entry<XDI3SubSegment, ZephyrContextNode>> it = this.contextNodes.entrySet().iterator();
			while (it.hasNext()) {
				Entry<XDI3SubSegment, ZephyrContextNode> entry = it.next();
				String Key = entry.getKey().toString();
				ZephyrUtils.doDelete(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "/" + Key + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
				it.remove(); // avoids a ConcurrentModificationException

			}
			
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "()", "");

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {
		try {

			String newContextNode = "[" +  targetContextNode.toString() + "]";

			Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);

			if (relations == null) {
				relations = new HashMap<XDI3Segment, ZephyrRelation>();
			}
			this.relations.put(arcXri, relations);

			// Check for relation in existing user graph.
			String graph = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			JSONObject jsonGraph = new JSONObject(graph);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (!key.equals("") && jsonGraph.getString(key).contains(targetContextNode.toString())) {
					throw new Xdi2GraphException("Relation already exists");
				} else if (key.equals(arcXri.toString())) {
					newContextNode = jsonGraph.getString(key).replace("[", "").replace("]", "");
					newContextNode = newContextNode.concat("," + targetContextNode);
					newContextNode = "[" + newContextNode + "]";
				}
			}

			// Create relation if it is not already exists.
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), newContextNode);
			ZephyrRelation relation = new ZephyrRelation(this.getGraph(), this);
			relation.setArcXri(arcXri);
			relation.setTargetContextNodeXri(XDI3Segment.create(targetContextNode.toString()));
			relations.put(targetContextNode.getXri(), relation);
			return relation;

		} catch (Xdi2GraphException e) {
			throw e;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		try {
			ReadOnlyIterator<Relation> itrReadOnlyRelations = null;
			if(this.relations.size() != 0)
			{
			List<Relation> relations = new ArrayList<Relation>();
			for (Entry<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> entry : this.relations.entrySet()) {
				log.debug(entry.getKey() + "/" + entry.getValue());
				Map<XDI3Segment, ZephyrRelation> relation = this.relations.get(entry.getKey());
				for (Entry<XDI3Segment, ZephyrRelation> entry1 : relation.entrySet()) {
					log.debug(entry1.getKey() + "/" + entry1.getValue());
					relations.add(entry1.getValue());
				}
			}

			itrReadOnlyRelations = new ReadOnlyIterator<Relation>(relations.iterator());
			return itrReadOnlyRelations;
			}
			else
			{
				String response = "";
				String[] arrrelations = null;
				String relations = null;
				response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

				log.info(response);
				JSONObject jsonGraph = new JSONObject(response);
				
				List<Relation> lstRelation = new ArrayList<Relation>();
				ZephyrRelation relation = null;

				Iterator<String> nodes = jsonGraph.keys();
				while (nodes.hasNext()) {
					String key = nodes.next();
					if (!key.equals("()") && !key.equals("<>")) {
						relations = jsonGraph.getString(key).replace("[", "").replace("]", "");
						if (relations != null && relations.split(",") != null) {
							arrrelations = relations.split(",");
							for (String rel : arrrelations) {
								relation = new ZephyrRelation(this.getGraph(), this);
								relation.setArcXri(XDI3Segment.create(rel));
								relation.setTargetContextNodeXri(XDI3Segment.create(key));
								lstRelation.add(relation);
							}

						}
					}

				}
				
				itrReadOnlyRelations = new ReadOnlyIterator<Relation>(lstRelation.iterator());

				return itrReadOnlyRelations;
				
			}
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		try {
			if(this.relations.size() !=0)
			{
			Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);
			relations.remove(targetContextNodeXri);
			}

			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			String relation = null;
			// Replace the relation value with null
			JSONObject jsonGraph = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();

			while (nodes.hasNext()) {
				String key = (String) nodes.next();
				if (key.equals(arcXri.toString())) {
					relation = jsonGraph.getString(key).replace("[", "").replace("]", ""); 
					relation = relation.replace(targetContextNodeXri.toString() + ",", "").replace(targetContextNodeXri.toString(), "");
					relation = StringUtils.removeEnd(relation, ",");
					relation = "[" + relation + "]";
					break;
				}
			}
			
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), relation);

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {
		try {
			if(this.relations.size() !=0)
				this.relations.remove(arcXri);

			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), null);
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteRelations() {
		try {

			if (this.relations.size() != 0) {
				for (Entry<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> entry : this.relations.entrySet()) {
					String Key = entry.getKey().toString();
					this.relations.remove(Key);

				}
			}
			
			String response = "";
			response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

			log.info(response);
			JSONObject jsonGraph = new JSONObject(response);
						
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (!key.equals("()") && !key.equals("<>")) {
					ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), key, null);
				}

			}
		
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Literal createLiteral(String literalData) {
		try {
			String contextNode = this.getArcXri().toString();

			String userGraph = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			// Check for context node in existing user graph.
			JSONObject jsonGraph = new JSONObject(userGraph);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (jsonGraph.getString(key).equals(literalData)) {
					throw new Xdi2GraphException("Literal already exists");
				}
			}

			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "<>", literalData);

			ZephyrLiteral literal = new ZephyrLiteral(this.getGraph(), this);
			literal.setLiteralData(literalData);
			return literal;

		} catch (Xdi2GraphException e) {
			throw e;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Literal getLiteral() {
		try {
			ZephyrLiteral literal = null;
			String contextNode = this.arcXri.toString();
			// this.objParentNode = this.getContextNode();
			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

			// Find for the literal
			JSONObject jsonGraph = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = (String) nodes.next();
				if (key.equals("<>")) {
					literal = new ZephyrLiteral(this.getGraph(), this);
					literal.setLiteralData(jsonGraph.getString(key));
				}
			}

			return literal;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteLiteral() {
		try {
			String contextNode = this.arcXri.toString();
			// this.objParentNode = this.getContextNode();
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + ((ZephyrGraph) this.getGraph()).getGraphIdentifier() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "<>", null);

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	private String contextNodePath() {
		lstArcXRIs = new ArrayList<String>();
		getListArcXri(this);
		return StringUtils.join(lstArcXRIs, "/");
	}

	private void getListArcXri(ContextNode objNode) {
		ZephyrContextNode objNode_Zephyr = (ZephyrContextNode) objNode;
		if (objNode_Zephyr.getContextNode().getArcXri() != null)
			getListArcXri(objNode_Zephyr.getContextNode());
		lstArcXRIs.add(objNode_Zephyr.getArcXri().toString());
	}

	private String parentContextNodePath() {
		lstArcXRIs = new ArrayList<String>();
		getListArcXri(this.getContextNode());
		return StringUtils.join(lstArcXRIs, "/");
	}

}