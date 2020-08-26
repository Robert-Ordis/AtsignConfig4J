package info.ro.gadget.atsignconfig.core.definition.member;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.exception.AcWrongClassException;
import info.ro.gadget.atsignconfig.core.instance.setter.MethodSetter;

/**
 * 実装者が細かい動きを指定したい場合の定義。<br>
 * かつ、パラメータ名が一定の法則に従って変わる場合に定義する。<br>
 * 対象のメンバーはMather１つ→String２つor１つとを引数とするメソッドに限定される。<br>
 * @author Robert_Ordis
 *
 */
public class RegexConfig implements AcMethod{
	
	
	@Override
	public MethodSetter makeMemberSetter(Class<? extends AtsignConfig> clazz, final String name, Method method)
			throws AcWrongClassException {
		// TODO Auto-generated method stub
		String key = clazz.getName() + "[" + name + "]";
		
		Class<?>[] a = method.getParameterTypes();
		boolean badFunc = false;
		switch(a.length) {
		case 3:
			if(!a[2].isAssignableFrom(String.class)) {
				badFunc = true;
			}
		case 2:
			if(!a[1].isAssignableFrom(String.class)
				|| !a[0].isAssignableFrom(Matcher.class)) {
				badFunc = true;
			}
			break;
		default:
			badFunc = true;
		}
		if(badFunc) {
			throw new AcWrongClassException(key, "Method must have arguments as follows: (Matcher match, String value, String comment) or (Matcher match, String value)");
		}
		
		//正規表現を付け加える匿名クラスを生成する。
		try {
			MethodSetter ret = new MethodSetter(method, a.length, name) {
				Pattern pattern = Pattern.compile(name);
				@Override
				public void putValueInside(AtsignConfig conf, Member member, String name, Object value, String comment)
						throws Exception {
					// TODO Auto-generated method stub
					String str = (String)value;
					Matcher m = this.pattern.matcher(name);
					Method method = (Method)member;
					try {
						if(m.find()) {
							switch(this.argc) {
							case 3:
								method.invoke(conf, m, str, comment);
								break;
							case 2:
								method.invoke(conf, m, str);
								break;
							}
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
		catch(PatternSyntaxException e) {
			throw new AcWrongClassException(key, e.getMessage());
		}
		
	}

}
