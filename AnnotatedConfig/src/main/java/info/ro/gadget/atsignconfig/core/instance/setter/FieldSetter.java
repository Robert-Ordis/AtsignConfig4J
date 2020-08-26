package info.ro.gadget.atsignconfig.core.instance.setter;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.annotation.Constraint;
import info.ro.gadget.atsignconfig.core.instance.deserializer.AcDeserializer;

@SuppressWarnings("rawtypes")
public abstract class FieldSetter implements MemberSetter{
	Field field;
	AcDeserializer des;
	Constraint constraint;
	Class clazz;
	String name;
	
	private static Set<Class> intClazz = new HashSet<Class>();
	private static Set<Class> doubleClazz = new HashSet<Class>();
	static {
		Collections.addAll(intClazz, Integer.class, int.class, Byte.class, byte.class, Short.class, short.class, Long.class, long.class);
		Collections.addAll(doubleClazz, Float.class, float.class, Double.class, double.class);
	}
	
	protected FieldSetter(Class clazz, Field member, String name){
		this.clazz = clazz;
		this.field = member;
		this.name = name;
	}
	
	public void setDeserializer(AcDeserializer d){
		this.des = d;
	}
	
	@Override
	public void setConstraint(Constraint c){
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
		return this.field;
	}
	
	public Class getMemberClass(){
		return this.clazz;
	}
	
	@Override
	abstract public void putValueInside(AtsignConfig conf, Member member, String name, Object value, String comment) throws Exception;
	
	@Override
	public void putValue(AtsignConfig conf, String name, String value, String comment) throws Exception{
		try {
			Object inst = des.deserialize(value);
			throwIfInvalid(inst, value);
			this.field.setAccessible(true);
			this.putValueInside(conf, this.field, name, inst, comment);
		}
		catch(Exception e) {
			//e.printStackTrace();
			if(this.constraint != null && (this.constraint.value().isInvalidBlock())) {
				throw e;
			}
		}
	}
	
	public void throwIfInvalid(Object value, String src) throws Exception{
		//プリミティブ/ラッパーである場合はconstraintに書いてあるのでそれを参照する。
		//それ以外である場合は文字列長とかいろいろやることはあるかもかも
		if(this.constraint == null) {
			return;
		}
		if(intClazz.contains(this.clazz)) {
			//Long val = Long.valueOf(src.trim());
			long val = ((Number)value).longValue();
			if(constraint.maxInt() < val || constraint.minInt() > val) {
				throw new Exception("value["+val+"] exceeds value limit.");
			}
		}
		else if(doubleClazz.contains(this.clazz)) {
			//Double val = Double.valueOf(src.trim());
			double val = ((Number)value).doubleValue();
			if(constraint.maxDouble() < val || constraint.minDouble() > val) {
				throw new Exception("value["+val+"] exceeds value limit.");
			}
		}
		else {
			int strLen = src.length();
			if(constraint.maxStrLen() < strLen || constraint.minStrLen() > strLen) {
				throw new Exception("value["+src+"] exceeds length limit.");
			}
		}
	}

}
