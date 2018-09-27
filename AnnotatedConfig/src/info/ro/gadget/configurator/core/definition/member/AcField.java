package info.ro.gadget.configurator.core.definition.member;

import java.lang.reflect.Field;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.exception.AcWrongClassException;
import info.ro.gadget.configurator.core.instance.setter.FieldSetter;

public interface AcField {
	public static final AcField SINGLE = new SingleConfig();
	public static final AcField ARGLESS = new ArglessConfig();
	//public static final AcField COLLECT = new CollectConfig();
	
	public FieldSetter makeMemberSetter(Class<? extends AnnotatedConfig> clazz, String name, Field field) throws AcWrongClassException;
}
