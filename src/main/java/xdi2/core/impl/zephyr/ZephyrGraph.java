package xdi2.core.impl.zephyr;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.AbstractGraph;

public class ZephyrGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;
	
	private ZephyrContextNode rootContextNode;
	private int sortmode;
 
 	ZephyrGraph(ZephyrGraphFactory graphFactory,int sortmode) {

		super(graphFactory);
		this.sortmode = sortmode;
		this.rootContextNode = new ZephyrContextNode(this, null);
		this.rootContextNode.arcXri = null;
	}

	@Override
	public ContextNode getRootContextNode() {
		// TODO Auto-generated method stub
		//return new ZephyrContextNode(this, null);
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
