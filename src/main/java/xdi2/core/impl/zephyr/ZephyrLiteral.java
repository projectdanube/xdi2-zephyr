package xdi2.core.impl.zephyr;

import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteral;

public class ZephyrLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3534354653137496233L;

	private String literalData;

	public ZephyrLiteral(ZephyrContextNode contextNode, String literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	@Override
	public String getLiteralData() {

		return this.literalData;
	}

	@Override
	public void setLiteralData(String literalData) {

		this.literalData = literalData;

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(((ZephyrContextNode) this.getContextNode()).contextNodePath(false), XDIConstants.XRI_S_LITERAL.toString(), literalData);
	}
}
