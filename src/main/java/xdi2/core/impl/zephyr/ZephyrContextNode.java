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
	ContextNode objParentNode;

	ZephyrContextNode(Graph graph, ContextNode contextNode) {
		super(graph, contextNode);
		this.relations = new HashMap<XDI3Segment, Map<XDI3Segment, ZephyrRelation>>();
		this.contextNodes = new HashMap<XDI3SubSegment, ZephyrContextNode> ();
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

			if (!this.getArcXri().toString().equals(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getGraphIdentifier())) {
				// parentContextnode= this.getArcXri().toString();

				// Check for context node in existing user graph.
				String userGraph = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
				JSONObject jsonGraph = new JSONObject(userGraph);
				Iterator<String> nodes = jsonGraph.keys();
				while (nodes.hasNext()) {
					String key = nodes.next();
					if (key.equals(arcXri.toString()) && !jsonGraph.getString(key).equals("null")) {
						throw new Xdi2GraphException("Context Node already exists");
					}
				}
				
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "/" + arcXri.toString() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "", "");
			} else {
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "/" + arcXri.toString() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "", "");
				this.objParentNode = null; // For Root Context Node, parent object will be null
			}

			ZephyrContextNode contextNode = new ZephyrContextNode(this.getGraph(), this);
			contextNode.arcXri = arcXri;
			contextNode.objParentNode = this;
			this.contextNodes.put(arcXri, contextNode);
			return contextNode;

		} catch (Xdi2GraphException e) {
			throw e;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(),e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		try {
//			String rootContextNode = this.arcXri.toString();
//			String response = "";
//			response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
//			
//			log.info(response);
//			JSONObject jsonGraph = new JSONObject(response);
//
//			ZephyrContextNode contextNode = null;
//			List<ContextNode> lstContextNode = new ArrayList<ContextNode>();
//
//			Iterator<String> nodes = jsonGraph.keys();
//			while (nodes.hasNext()) {
//				String key = nodes.next();
//				contextNode = new ZephyrContextNode(this.getGraph(), this);
//				if (!key.equals("")) {
//					contextNode.arcXri = XDI3SubSegment.create(key);
//					lstContextNode.add(contextNode);
//				}
//			}
//
//			ReadOnlyIterator<ContextNode> itrContextNodes = new ReadOnlyIterator<ContextNode>(lstContextNode.iterator());
//
//			return itrContextNodes;
			return new ReadOnlyIterator<ContextNode> (new CastingIterator<ZephyrContextNode, ContextNode> (this.contextNodes.values().iterator()));
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		try {
			this.objParentNode = this.getContextNode();
			// Put the context node value as null to zephyr store
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), null);

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteContextNodes() {
		try {
			this.objParentNode = this.getContextNode();
			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

			// Replace all relation values with null
			JSONObject jsonGraph = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = (String) nodes.next();
				if (!key.equals("") && !this.relations.containsKey(XDI3SubSegment.create(key))) {
					ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), key, null);
				}
			}

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {
		try {

			String newContextNode = targetContextNode.toString();

			Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);

			if (relations == null) {
				relations = new HashMap<XDI3Segment, ZephyrRelation>();
			}
			this.relations.put(arcXri, relations);

			// Check for relation in existing user graph.
			String graph = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			JSONObject jsonGraph = new JSONObject(graph);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (!key.equals("") && jsonGraph.getString(key).contains(targetContextNode.toString())) {
					throw new Xdi2GraphException("Relation already exists");
				} else if (key.equals(arcXri.toString())) {
					newContextNode = jsonGraph.getString(key).concat("," + targetContextNode);
				}
			}

			// Create relation if it is not already exists.
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), newContextNode);
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
			List<Relation> relations = new ArrayList<Relation>();
			for (Entry<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> entry : this.relations.entrySet()) {
				log.debug(entry.getKey() + "/" + entry.getValue());
				Map<XDI3Segment, ZephyrRelation> relation = this.relations.get(entry.getKey());
				for (Entry<XDI3Segment, ZephyrRelation> entry1 : relation.entrySet()) {
					log.debug(entry1.getKey() + "/" + entry1.getValue());
					relations.add(entry1.getValue());
				}
			}

			ReadOnlyIterator<Relation> itrReadOnlyRelations = new ReadOnlyIterator<Relation>(relations.iterator());
			return itrReadOnlyRelations;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		try {
			Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);
			relations.remove(targetContextNodeXri);

			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			// Replace the relation value with null
			JSONObject jsonGraph = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();

			while (nodes.hasNext()) {
				String key = (String) nodes.next();
				if (key.equals(arcXri.toString())) {
					ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), jsonGraph.getString(key).replace("," + targetContextNodeXri.toString(), "").replace(targetContextNodeXri.toString(), ""));
				}
			}

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {
		try {
			this.relations.remove(arcXri);

			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), null);
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteRelations() {
		try {

			for (Entry<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> entry : this.relations.entrySet()) {
				String Key = entry.getKey().toString();
				ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), Key, null);
				this.relations.remove(Key);

			}

			// Get user graph
			// String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/" + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());

		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Literal createLiteral(String literalData) {
		try {
			String contextNode = this.getArcXri().toString();

			String userGraph = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());
			// Check for context node in existing user graph.
			JSONObject jsonGraph = new JSONObject(userGraph);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = nodes.next();
				if (jsonGraph.getString(key).equals(literalData)) {
					throw new Xdi2GraphException("Literal already exists");
				}
			}

			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "!", literalData);

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
			this.objParentNode = this.getContextNode();
			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken());

			// Find for the literal
			JSONObject jsonGraph = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
			while (nodes.hasNext()) {
				String key = (String) nodes.next();
				if (key.equals("!")) {
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
			this.objParentNode = this.getContextNode();
			ZephyrUtils.doPut(((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getDataApi() + "/" + contextNodePath() + "?token=" + ((ZephyrGraphFactory) this.getGraph().getGraphFactory()).getOauthToken(), "!", null);

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
		if (objNode_Zephyr.objParentNode != null)
			getListArcXri(objNode_Zephyr.objParentNode);
		lstArcXRIs.add(objNode_Zephyr.getArcXri().toString());
	}

	private String parentContextNodePath() {
		lstArcXRIs = new ArrayList<String>();
		getListArcXri(this.objParentNode);
		return StringUtils.join(lstArcXRIs, "/");
	}

}