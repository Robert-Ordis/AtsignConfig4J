package info.ro.gadget.configurator.core.definition.member;

import java.lang.reflect.Method;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.exception.AcWrongClassException;
import info.ro.gadget.configurator.core.instance.setter.MethodSetter;

public interface AcMethod {
	
	public static final AcMethod SETTER = new SetterConfig();
	public static final AcMethod REGEX = new RegexConfig();
	
	public MethodSetter makeMemberSetter(Class<? extends AnnotatedConfig> clazz, String name, Method method) throws AcWrongClassException;
}
