package xdi2.core.impl.zephyr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;
	private static final Logger log = LoggerFactory.getLogger(ZephyrContextNode.class);
	
	XDI3SubSegment arcXri;
	
	private Map<XDI3SubSegment, ZephyrContextNode> contextNodes;
	private Map<XDI3Segment, Map<XDI3Segment, ZephyrRelation>> relations;
	private static ZephyrLiteral literal = null;
	
	// Store the current URL path
	private static String urlPath = ""; 

	ZephyrContextNode(Graph graph, ContextNode contextNode) {
		super(graph, contextNode);
	}

	@Override
	public XDI3SubSegment getArcXri() {
		return this.arcXri;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {
		try {			
			
			String userGraph = ZephyrGraphFactory.userGraph;
//			if(ZephyrUtils.searchJson(userGraph, arcXri.toString()) != null)
//			{
//				throw new Xdi2GraphException("Context Node already exists");
//			}

			ZephyrContextNode contextNode = new ZephyrContextNode(this.getGraph(), this);
			contextNode.arcXri = arcXri;
			urlPath = urlPath + "/" + arcXri;
			
			String[] contextNodes = urlPath.split("/");
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNodes[contextNodes.length-1] + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), "", "");
			return contextNode;
					
		 } catch (Exception e) {
			 throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		try {
		String[] contextNodes = urlPath.split("/");
		String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNodes[1] + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		log.info(response);
		JSONObject jsonGraph  = new JSONObject(response);
		
		ZephyrContextNode contextNode = null;
		List<ContextNode> lstContextNode = new ArrayList<ContextNode>();
		
		Iterator<String> nodes = jsonGraph.keys();
		while(nodes.hasNext()){
			String key = nodes.next();
			contextNode = new ZephyrContextNode(this.getGraph(), this);
			if(!key.equals("") )
			{
			contextNode.arcXri = XDI3SubSegment.create(key);
			lstContextNode.add(contextNode);
			}
		}
		
		ReadOnlyIterator<ContextNode> itrContextNodes = new ReadOnlyIterator<ContextNode>(lstContextNode.iterator());
				
		return itrContextNodes;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		ContextNode contextNode = this.getGraph().getRootContextNode().getContextNode(arcXri);
		try {
			ZephyrUtils.doDelete(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + contextNode + "/"+ arcXri + "?token="+((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNodes() {
		try {
			ZephyrUtils.doDelete(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + this.getGraph().getRootContextNode().toString() + "/*" + "?token=" + ((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}

	}

	@Override
	public Relation createRelation(XDI3Segment arcXri,ContextNode targetContextNode) {
		try {
			String userGraph = ZephyrGraphFactory.userGraph;
//			if(ZephyrUtils.searchJson(userGraph, arcXri.toString()) != null)
//			{
//				throw new Xdi2GraphException("Relation already exists");
//			}
			
			String[] contextNodes = urlPath.split("/");
			
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNodes[1] + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), targetContextNode.toString() );
			ZephyrRelation relation = new ZephyrRelation(this.getGraph(), this);
			relation.setArcXri(arcXri);
			relation.setTargetContextNodeXri(XDI3Segment.create(targetContextNode.toString()));
			return relation;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		try {
			String[] contextNodes = urlPath.split("/");
			
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNodes[1] + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
			JSONObject jsonGraph  = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
			ZephyrRelation relation = null;
			List<Relation> relations = new ArrayList<Relation>();
			
			while(nodes.hasNext()){
		        String key = (String)nodes.next();
		        if(key.startsWith("+"))
		        {
		        	relation = new ZephyrRelation(this.getGraph(), this);
		        	relation.setArcXri(XDI3Segment.create(key));
		        	relation.setTargetContextNodeXri(XDI3Segment.create(jsonGraph.getString(key)));
		        	relations.add(relation);
		        }
		     }
			 ReadOnlyIterator<Relation> itrReadOnlyRelations = new ReadOnlyIterator<Relation>(relations.iterator());
			return itrReadOnlyRelations;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);
		if (relations == null) return;

		relations.remove(targetContextNodeXri);

		if (relations.isEmpty()) {

			this.relations.remove(arcXri);
		}

	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {
		this.relations.remove(arcXri);

	}

	@Override
	public void deleteRelations() {
		this.relations.clear();

	}

	@Override
	public Literal createLiteral(String literalData) {
		try {
			String[] contextNodes = urlPath.split("/");
			String keyContextNode = contextNodes[contextNodes.length -1];
			
			String userGraph = ZephyrGraphFactory.userGraph;
//			if(ZephyrUtils.searchJson(userGraph, keyContextNode) != null)
//			{
//				throw new Xdi2GraphException("Literal already exists");
//			}
			
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNodes[1] + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), keyContextNode, literalData );
			
			ZephyrLiteral literal = new ZephyrLiteral(this.getGraph(), this);
			literal.setLiteralData(literalData);
			this.literal = literal;
			return literal;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public Literal getLiteral() {
		return literal;
	}

	@Override
	public void deleteLiteral() {
		literal = null;

	}

	
}
