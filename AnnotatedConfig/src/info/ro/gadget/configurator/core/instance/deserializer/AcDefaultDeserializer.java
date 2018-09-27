package info.ro.gadget.configurator.core.instance.deserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;


/**
 * 例えばフィールドのクラスにデシリアライザを設定していなかった場合、<br>
 * 自動で「文字列」からなるコンストラクタを探してセットさせるための物<br>
 * @author Robert_Ordis
 *
 * @param <T>
 */
public class AcDefaultDeserializer<T extends Object> implements AcDeserializer{
	
	Constructor<T> con;
	
	public AcDefaultDeserializer(Class<T> cls) throws NoSuchMethodException, SecurityException{
		int mod = cls.getModifiers();
		if(Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
			throw new NoSuchMethodException("Member must be concrete class. Or, register specific deserializer for it.");
		}
		Constructor<T> c = cls.getDeclaredConstructor(String.class);
		if(!Modifier.isPublic(c.getModifiers()) && !c.isAccessible()) {
			throw new NoSuchMethodException("Member's constructor must be public.");
		}
		con = c;
	}
	
	@Override
	public T deserialize(String s) throws Exception{
		// TODO Auto-generated method stub
		try {
			return con.newInstance(s);
		}
		catch(InvocationTargetException e) {
			Throwable c = e.getTargetException();
			if(e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)c;
			}
			else {
				throw (Exception)c;
			}
		}
	}
	
}
