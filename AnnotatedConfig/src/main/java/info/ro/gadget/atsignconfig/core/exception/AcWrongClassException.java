package info.ro.gadget.atsignconfig.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * コンフィグ設計時、間違ったクラス記述が発覚した場合に投げる例外。<br>
 * そのような場合はそもそも「クラスを直せ」という場合なので検査対象に入らない。
 * 本当は「コンパイル時にチェックしてビルドエラーとする」というのが理想だけど、さすがにめんどくさい。
 * @author Robert_Ordis
 *
 */
public class AcWrongClassException extends RuntimeException{
	Map<String, String> causeParams = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2358092508286855136L;
	public AcWrongClassException(String key, String detail) {
		causeParams = new HashMap<String,String>();
		causeParams.put(key, detail);
	}
	public void merge(AcWrongClassException other) {
		for(String key : other.causeParams.keySet()) {
			this.causeParams.put(key, other.causeParams.get(key));
		}
	}
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Class/Params Error has detected as follows.");
		for(String key : this.causeParams.keySet()) {
			sb.append(System.getProperty("line.separator"));
			sb.append(key);
			sb.append(":");
			sb.append(this.causeParams.get(key));
		}
		return sb.toString();
	}
}
