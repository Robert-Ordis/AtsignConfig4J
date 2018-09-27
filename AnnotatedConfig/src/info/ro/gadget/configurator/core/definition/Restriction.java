package info.ro.gadget.configurator.core.definition;

/**
 * 入力値チェックに関する定数群。<br>
 * MUST/VALIDにて、変なことが判明したらコンフィグは無効となり、例外が投げられる。<br>
 * @author Robert_Ordis
 *
 */

public enum Restriction{
	NORMAL(false,false),
	VALID(false, true),
	MUST(true, true),
	;
	boolean required;
	boolean invalidBlock;
	private Restriction(boolean required, boolean invalidBlock) {
		this.required = required;
		this.invalidBlock = invalidBlock;
	}
	public boolean isRequired() {
		return required;
	}
	public boolean isInvalidBlock() {
		return invalidBlock;
	}
}
