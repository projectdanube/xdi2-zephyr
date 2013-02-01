package xdi2.core.impl.cloudfabric;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraph;

public class CloudFabricGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = -8716740616499117574L;

	CloudFabricGraph(GraphFactory graphFactory) {

		super(graphFactory);
	}

	@Override
	public ContextNode getRootContextNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
