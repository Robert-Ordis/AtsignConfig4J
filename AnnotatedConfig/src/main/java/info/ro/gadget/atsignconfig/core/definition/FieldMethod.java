package info.ro.gadget.atsignconfig.core.definition;

import info.ro.gadget.atsignconfig.core.definition.member.AcField;

/**
 * パラメータ取り扱いメソッドをクラス化するにあたって、<br>
 * enumにしないとアノテーションに入らないのでenum化。
 * @author Robert_Ordis
 *
 */
public enum FieldMethod {
	SINGLE(AcField.SINGLE),
	ARGLESS(AcField.ARGLESS),
	;
	
	AcField m;
	private FieldMethod(AcField m) {
		this.m = m;
	}
	public AcField getMethod() {
		return this.m;
	}
	
}
