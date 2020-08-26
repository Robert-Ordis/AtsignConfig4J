package info.ro.gadget.atsignconfig.core.definition.member;

import java.lang.reflect.Method;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.exception.AcWrongClassException;
import info.ro.gadget.atsignconfig.core.instance.setter.MethodSetter;

public interface AcMethod {
	
	public static final AcMethod SETTER = new SetterConfig();
	public static final AcMethod REGEX = new RegexConfig();
	
	public MethodSetter makeMemberSetter(Class<? extends AtsignConfig> clazz, String name, Method method) throws AcWrongClassException;
}
