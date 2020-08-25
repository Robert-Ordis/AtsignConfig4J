package info.ro.gadget.annotatedconfig.ratconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatName {
	
	Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
	String name;
	public RatName(String name) throws Exception {
		log.info("this name is {}", name);
		this.name = name;
	}
	@Override
	public String toString() {
		return name == null ? "What the...":"my name is ["+name+"]";
	}
}
