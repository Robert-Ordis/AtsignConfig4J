package info.ro.gadget.configurator.core.definition.member;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.exception.AcWrongClassException;
import info.ro.gadget.configurator.core.instance.setter.MethodSetter;

/**
 * 実装者が細かい動きを指定したい場合の定義。<br>
 * 対象のメンバーはString１つかString２つを引数とするメソッドに限定される。<br>
 * @author Robert_Ordis
 *
 */
public class SetterConfig implements AcMethod{
	
	/**
	 * 
	 */
	@Override
	public MethodSetter makeMemberSetter(Class<? extends AnnotatedConfig> clazz, String name, Method method)
			throws AcWrongClassException {
		// TODO Auto-generated method stub
		String key = clazz.getName() + "[" + name + "]";
		
		Class<?>[] a = method.getParameterTypes();
		boolean badFunc = false;
		switch(a.length) {
		case 2:
			if(!a[1].isAssignableFrom(String.class)) {
				badFunc = true;
			}
		case 1:
			if(!a[0].isAssignableFrom(String.class)) {
				badFunc = true;
			}
			break;
		default:
			badFunc = true;
		}
		if(badFunc) {
			//文字列１つか２つのみを引数とするのが条件である。
			throw new AcWrongClassException(key, "Method must have arguments as follows: (String value, String comment) or (String value)");
		}
		
		MethodSetter ret = new MethodSetter(method, a.length, name) {
			@Override
			public void putValueInside(AnnotatedConfig conf, Member member, String name, Object value, String comment)
					throws Exception {
				// TODO Auto-generated method stub
				String str = (String)value;
				Method method = (Method)member;
				try {
					switch(this.argc) {
					case 2:
						method.invoke(conf, str, comment);
						break;
					case 1:
						method.invoke(conf, str);
						break;
					}
				}catch(InvocationTargetException e) {
					if(e.getTargetException() instanceof Exception) {
						throw (Exception)e.getTargetException();
					}
					else {
						throw (Error) e.getTargetException();
					}
				}
			}
			
		};
		
		return ret;
	}

}
