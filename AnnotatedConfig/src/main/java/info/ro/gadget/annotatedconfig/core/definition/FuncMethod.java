package info.ro.gadget.annotatedconfig.core.definition;

import info.ro.gadget.annotatedconfig.core.definition.member.AcMethod;

public enum FuncMethod {
	SETTER(AcMethod.SETTER),
	REGEX(AcMethod.REGEX),
	;
	AcMethod m;
	private FuncMethod(AcMethod m) {
		this.m = m;
	}
	public AcMethod getMethod() {
		return this.m;
	}
}
