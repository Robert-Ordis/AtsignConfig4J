package info.ro.gadget.atsignconfig.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ro.gadget.atsignconfig.core.instance.AcConfigSetterStore;
import info.ro.gadget.atsignconfig.core.instance.deserializer.DeserializerStore;
import info.ro.gadget.atsignconfig.reader.AtsignConfigLineListener;

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
		stores = new HashMap<Class<? extends AtsignConfig>, AcConfigSetterStore>();
	}
	
	
	private class ConfigLineListenerImpl implements AtsignConfigLineListener{

		@Override
		public void onValue(int lineNo, String name, String value, String comment) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pushSubset(int lineNo, String name, String comment) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void popSubset(int lineNo) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clearSubset() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void switchSection(int lineNo, String name, String comment) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
