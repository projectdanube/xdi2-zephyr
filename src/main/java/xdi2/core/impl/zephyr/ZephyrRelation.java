package xdi2.core.impl.zephyr;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;



public class ZephyrRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -4711688892198885875L;
	
	XDI3Segment arcXri;
	XDI3Segment targetContextNodeXri;

	ZephyrRelation(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);
	}

	@Override
	public XDI3Segment getArcXri() {
		return this.arcXri;
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {
		
		return this.targetContextNodeXri;
	}

	public void setArcXri(XDI3Segment arcXri) {
		this.arcXri = arcXri;
	}

	public void setTargetContextNodeXri(XDI3Segment targetContextNodeXri) {
		this.targetContextNodeXri = targetContextNodeXri;
	}
	
}
