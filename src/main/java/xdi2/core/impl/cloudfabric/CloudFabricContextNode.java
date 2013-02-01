package xdi2.core.impl.cloudfabric;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class CloudFabricContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;

	CloudFabricContextNode(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);
	}

	@Override
	public XDI3SubSegment getArcXri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteContextNodes() {
		// TODO Auto-generated method stub

	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {
		// TODO Auto-generated method stub
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
