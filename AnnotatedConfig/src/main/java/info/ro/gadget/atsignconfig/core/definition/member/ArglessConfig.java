package info.ro.gadget.atsignconfig.core.definition.member;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.exception.AcWrongClassException;
import info.ro.gadget.atsignconfig.core.instance.setter.FieldSetter;

/**
 * 値を持たず、パラメータ名だけ書くようなコンフィグの指定。<br>
 * この値が相手にするのはBoolean型のみである。
 * @author Robert_Ordis
 *
 */
public class ArglessConfig implements AcField {
	
	Logger log = LoggerFactory.getLogger(new Object(){}.getClass().getEnclosingClass());
	
	@Override
	public FieldSetter makeMemberSetter(Class<? extends AtsignConfig> clazz, final String name, final Field field, final boolean hidden)
			throws AcWrongClassException {
		// TODO Auto-gene// TODO Auto-generated method stub
		String key = clazz.getName() + "[" + name + "]";
		
		
		//クラスの検査。プリミティブならラッパークラスに直したうえで実行。（プリミティブのラッパーはVoidを除いて文字列型から変換できる）
		Class<?> fieldClazz = field.getType();
		
		if(!fieldClazz.equals(Boolean.class) && !fieldClazz.equals(boolean.class)) {
			throw new AcWrongClassException(key, "Member type must be boolean/Boolean.");
		}
		
		//ただし、どのような値をもらったところでTrueしかセットしないため、デシリアライザはStringとなる。
		FieldSetter ret = new FieldSetter(String.class, field, name) {
			@Override
			public void putValueInside(AtsignConfig conf, Member member, String name, Object value, String comment)
					throws Exception {
				// TODO Auto-generated method stub
				log.info("{} :argless input", name);
				field.set(conf, true);
			}
			
			@Override
			public boolean isHidden () {return hidden;}
			
		};
		return ret;
	}

}
