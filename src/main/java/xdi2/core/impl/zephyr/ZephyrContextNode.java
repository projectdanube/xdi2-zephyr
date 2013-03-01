package xdi2.core.impl.zephyr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.memory.MemoryLiteral;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;
	
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
			ZephyrContextNode contextNode = new ZephyrContextNode(this.getGraph(), this);
			contextNode.arcXri = arcXri;
			urlPath = urlPath + "/" + arcXri;
			return contextNode;
					
		 } catch (Exception e) {
			 throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		try {
		String response = ZephyrUtils.doGet(getUrl());
		System.out.println(response);
		JSONObject jsonGraph  = new JSONObject(response);
		Iterator<ContextNode> nodes = jsonGraph.keys();
		ReadOnlyIterator<ContextNode> itrContextNodes = new ReadOnlyIterator<ContextNode>(nodes);
		return itrContextNodes;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		ContextNode contextNode = this.getGraph().getRootContextNode().getContextNode(arcXri);
		try {
			ZephyrUtils.doDelete(ZephyrGraphFactory.DEFAULT_DATA_API + contextNode + "/"+ arcXri + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN);
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNodes() {
		try {
			ZephyrUtils.doDelete(ZephyrGraphFactory.DEFAULT_DATA_API + this.getGraph().getRootContextNode().toString() + "/*" + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN);
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}

	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		try {
			
			ZephyrUtils.doPut(getUrl(), arcXri.toString(), targetContextNodeXri.toString() );
			ZephyrRelation relation = new ZephyrRelation(this.getGraph(), this);
			relation.setArcXri(arcXri);
			relation.setTargetContextNodeXri(targetContextNodeXri);
			return relation;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		try {
			String response = ZephyrUtils.doGet(getUrl());
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
		        	//relation.add(key + "/" + jsonGraph.getString(key));
		        }
		     }
			Iterator<Relation> itrRelation = relations.iterator();
			while(itrRelation.hasNext())
			{
				System.out.println(itrRelation.next());
			}
			
			 ReadOnlyIterator<Relation> itrReadOnlyRelations = new ReadOnlyIterator<Relation>(itrRelation);
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
			ZephyrUtils.doPut(getUrl(), keyContextNode, literalData );
			
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

	@Override
	public Relation createRelation(XDI3Segment arcXri,ContextNode targetContextNode) {
			return this.createRelation(arcXri, targetContextNode);
	}
	
	private String getUrl()
	{
		String[] contextNodes = urlPath.split("/");
		String URL = ZephyrGraphFactory.DEFAULT_DATA_API  + ZephyrGraphFactory.rootNode + "/" + contextNodes[1] + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN;
		return URL;
	}
		
	
}
