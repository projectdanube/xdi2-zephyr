package xdi2.core.impl.zephyr;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class ZephyrLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3534354653137496233L;
	private String literalData;

	public ZephyrLiteral(Graph graph, ContextNode contextNode) {
		super(graph, contextNode);
	
	}

	@Override
	public String getLiteralData() {
		return this.literalData;
	}

	@Override
	public void setLiteralData(String literalData) {
		this.literalData = literalData;

	}
}
