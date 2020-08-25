package info.ro.gadget.annotatedconfig.ratconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcretePart extends AbstPart{

	Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
	public ConcretePart(String str) {
		super(str);
		log.info("あいうえお");
	}
}
