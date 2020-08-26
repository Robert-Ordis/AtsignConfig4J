package info.ro.gadget.atsignconfig.ratconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstPart {
	
	Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
	public AbstPart(String string) {
		log.info("Abst constructor");
	}
}
