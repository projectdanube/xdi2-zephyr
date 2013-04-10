package xdi2.core.impl.zephyr;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.xri3.XDI3SubSegment;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;
	
	private ZephyrContextNode rootContextNode;
	private int sortmode;
 
 	ZephyrGraph(ZephyrGraphFactory graphFactory) {

		super(graphFactory);
		this.rootContextNode = new ZephyrContextNode(this, null);
		this.rootContextNode.arcXri = null;
	}

	@Override
	public ContextNode getRootContextNode() {
		return this.rootContextNode;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	
	public String toString()
	{
		return "{}";
	}
	
	int getSortMode() {

		return this.sortmode;
	}
	
}
