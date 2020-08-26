package info.ro.gadget.atsignconfig.core.exception;

import java.util.HashMap;
import java.util.Map;

public class AcWrongParamException extends Exception{
	Map<String, Exception> causeParams = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2358092508286855136L;
	public AcWrongParamException() {
		causeParams = new HashMap<String, Exception>();
	}
	
	public void put(String key, Exception exception) {
		this.causeParams.put(key, exception);
	}
	
	public void merge(AcWrongParamException other) {
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
			sb.append("->");
			sb.append(this.causeParams.get(key).getMessage());
		}
		return sb.toString();
	}
	
	public void throwIfExist() throws AcWrongParamException{
		if(!this.causeParams.isEmpty()) {
			throw this;
		}
	}
}
