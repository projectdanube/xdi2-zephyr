package xdi2.core.impl.zephyr;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.XDI3Segment;



public class ZephyrRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -4711688892198885875L;

	ZephyrRelation(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);
	}

	@Override
	public XDI3Segment getArcXri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {
		// TODO Auto-generated method stub
		return null;
	}
}
