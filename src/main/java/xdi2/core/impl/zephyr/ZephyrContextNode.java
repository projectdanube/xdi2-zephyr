package xdi2.core.impl.zephyr;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.XRIUtil;
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
		// TODO Auto-generated method stub
		try {			
			ZephyrUtils.doPut("http://107.21.179.68:10002/"+ arcXri.toString() + "/?token=SECRET", arcXri.toString(), "" );
			
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
		

	}

	@Override
	public void deleteContextNodes() {
		// TODO Auto-generated method stub

	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {
		// TODO Auto-generated method stub
		try {
			ZephyrUtils.doPut("http://107.21.179.68:10002/"+ arcXri.toString() + "/?token=SECRET", arcXri.toString(), targetContextNode.toString() );
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		return null;
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRelations() {
		// TODO Auto-generated method stub

	}

	@Override
	public Literal createLiteral(String literalData) {
		// TODO Auto-generated method stub
		try {
			ZephyrUtils.doPut("http://107.21.179.68:10002/"+ arcXri.toString() + "/?token=SECRET", "Key", literalData );
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		return null;
	}

	@Override
	public Literal getLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLiteral() {
		// TODO Auto-generated method stub

	}
	
	
	
}
