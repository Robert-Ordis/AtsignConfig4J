package info.ro.gadget.configurator.core.instance.setter;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.annotation.Constraint;
import info.ro.gadget.configurator.core.definition.Restriction;

public abstract class MethodSetter implements MemberSetter{
	Method method;
	Constraint constraint;
	protected int argc;
	String name;
	
	protected MethodSetter(Method member, int argc, String name){
		this.method = member;
		this.argc = argc;
		this.name = name;
	}
	
	@Override
	public void setConstraint(Constraint c) {
		// TODO Auto-generated method stub
		this.constraint = c;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isSplitNeeded() {
		return false;
	}
	
	@Override
	public Member getMember() {
		return this.method;
	}

	@Override
	public abstract void putValueInside(AnnotatedConfig conf, Member member, String name, Object value, String comment) throws Exception;

	@Override
	public void putValue(AnnotatedConfig conf, String name, String value, String comment) throws Exception {
		// TODO Auto-generated method stub
		try {
			throwIfInvalid(value);
			this.method.setAccessible(true);
			this.putValueInside(conf, this.method, name, value, comment);
		}
		catch(Exception e) {
			if(this.constraint != null && (this.constraint.value().isInvalidBlock())) {
				throw e;
			}
		}
	}
	private void throwIfInvalid(String src) throws Exception{
		if(this.constraint == null) {
			return;
		}
		
		int strLen = src.length();
		if(constraint.maxStrLen() < strLen || constraint.minStrLen() > strLen) {
			throw new Exception("value["+src+"] exceeds length limit.");
		}

	}
}
