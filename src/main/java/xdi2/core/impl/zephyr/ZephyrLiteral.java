package xdi2.core.impl.zephyr;

import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteral;

import com.google.gson.JsonArray;

public class ZephyrLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3534354653137496233L;

	private Object literalData;

	public ZephyrLiteral(ZephyrContextNode contextNode, Object literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	@Override
	public Object getLiteralData() {

		return this.literalData;
	}

	@Override
	public void setLiteralData(Object literalData) {

		JsonArray array = new JsonArray();
		array.add(AbstractLiteral.literalDataToJsonPrimitive(literalData));

		// Zephyr request

		((ZephyrGraph) this.getGraph()).doPut(ZephyrContextNode.contextNodePath(this.getContextNode(), false), XDIConstants.XRI_S_LITERAL.toString(), array);

		// done

		this.literalData = literalData;
	}
}
