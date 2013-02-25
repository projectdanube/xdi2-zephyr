package xdi2.core.impl.zephyr;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.XRIUtil;
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
	private ZephyrLiteral literal;

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
			//ZephyrUtils.doPut(ZephyrGraphFactory.DEFAULT_DATA_API+ arcXri.toString() + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN	, arcXri.toString(), "" );
			
			if (XRIUtil.isIllegalContextNodeArcXri(arcXri)) throw new Xdi2GraphException("Invalid context node: " + arcXri);

			ZephyrContextNode contextNode = new ZephyrContextNode(this.getGraph(), this);
			contextNode.arcXri = arcXri;

			this.contextNodes.put(arcXri, contextNode);

			return contextNode;
					
		 } catch (Exception e) {
			 throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		return this.getGraph().getRootContextNode().getAllContextNodes();
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
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {
		try {
			ZephyrUtils.doPut(ZephyrGraphFactory.DEFAULT_DATA_API + arcXri.toString() + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN, arcXri.toString(), targetContextNode.toString() );
			
			if (arcXri == null) throw new NullPointerException();
			if (targetContextNode == null) throw new NullPointerException();
			
			XDI3Segment targetContextNodeXri = targetContextNode.getXri();

			if (XRIUtil.isIllegalRelationArcXri(arcXri, targetContextNodeXri)) throw new Xdi2GraphException("Invalid relation: " + arcXri + "/" + targetContextNodeXri);

			if (this.containsRelation(arcXri, targetContextNodeXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the relation " + arcXri + "/" + targetContextNodeXri + ".");

			Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);
			relations = new HashMap<XDI3Segment, ZephyrRelation> ();
			this.relations.put(arcXri, relations);
			
			ZephyrRelation relation = new ZephyrRelation(this.getGraph(), arcXri, this);
			relations.put(targetContextNodeXri, relation);

			return relation;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		Map<XDI3Segment, ZephyrRelation> relations = this.relations.get(arcXri);
		if (relations == null) return new EmptyIterator<Relation> ();

		return new ReadOnlyIterator<Relation> (new CastingIterator<ZephyrRelation, Relation> (relations.values().iterator()));
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
			ZephyrUtils.doPut(ZephyrGraphFactory.DEFAULT_DATA_API + arcXri.toString() + ZephyrGraphFactory.DEFAULT_OAUTH_TOKEN, "Key", literalData );
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		return null;
	}

	@Override
	public Literal getLiteral() {
		return this.literal;
	}

	@Override
	public void deleteLiteral() {
		this.literal = null;

	}
		
	
}
