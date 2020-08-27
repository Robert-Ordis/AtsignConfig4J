package info.ro.gadget.atsignconfig.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ro.gadget.atsignconfig.core.instance.AcConfigSetterStore;
import info.ro.gadget.atsignconfig.core.instance.deserializer.DeserializerStore;

/**
 * Config class Assembler.
 * ->Specify the source, reader, section, root level, and the class.
 * ->Due to quantity of this params, it is recommended to define the factory of this.
 * @author Robert_Ordis
 *
 */
public class AtsignConfigAssembler {

	private static Map<Class<? extends AtsignConfig>, AcConfigSetterStore> stores;
	DeserializerStore dstore;
	Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
	static {
		
	}
	
	
	private class ConfigLineListenerImpl {
		
	}
	
}
