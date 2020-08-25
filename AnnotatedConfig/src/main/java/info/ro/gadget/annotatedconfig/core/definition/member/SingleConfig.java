package info.ro.gadget.annotatedconfig.core.definition.member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.ro.gadget.annotatedconfig.core.AnnotatedConfig;
import info.ro.gadget.annotatedconfig.core.exception.AcWrongClassException;
import info.ro.gadget.annotatedconfig.core.instance.setter.FieldSetter;

/**
 * 値を一つ定義するようなコンフィグへの動作<br>
 * もしくはコレクションも含まれる（統一しました）<br>
 * これが相手にするのは具象クラスであり、<br>
 * かつコンストラクタについて引数がString一つであるものが定義されている場合のみである<br>
 * メンバーがnullだった場合はクラスを新しく作ってから入れる。<br>
 * 
 * @author Robert_Ordis
 *
 */
public class SingleConfig implements AcField{
	
	public static Class<?> extractGenericArgOfCollection(Field field) {
		ParameterizedType paramType = (ParameterizedType) field.getGenericType();
		if(paramType.getActualTypeArguments().length <= 0) {
			return null;
		}
		Type ret = paramType.getActualTypeArguments()[0];
		if(ret instanceof WildcardType) {
			return null;
		}
		return (Class<?>)ret;
	}
	
	private FieldSetter makeCollectionSetter(Class<? extends AnnotatedConfig> clazz, String name, Field field) 
		throws AcWrongClassException {

		String key = clazz.getName() + "[" + name + "]";
		//Extract the parameter type of the member(=collection).
		Class<?> genCls = extractGenericArgOfCollection(field);
		if(genCls == null) {
			throw new AcWrongClassException(key, "Param class must be clearly specified.");
		}
		FieldSetter ret = new FieldSetter(genCls, field, name) {
			@Override
			@SuppressWarnings("unchecked")
			public void putValueInside(AnnotatedConfig conf, Member member, String name, Object value, String comment)
					throws Exception {
				Collection<Object> existVal = (Collection<Object>) field.get(conf);
				
				if(existVal == null) {
					//もし初期宣言をさぼってnullだったりしたらこっちで勝手に新しく作る。
					try {
						//Praying for the member is represented as Concrete collection class.
						Constructor<Collection<Object>> con = (Constructor<Collection<Object>>) field.getType().getDeclaredConstructor();
						existVal = (Collection<Object>) con.newInstance();
					}catch(Exception e) {
						//If not, then, create ArrayList for List/HashSet for Set.
						if(List.class.isAssignableFrom(field.getType())){
							existVal = new ArrayList<Object>();
						}
						else if(Set.class.isAssignableFrom(field.getType())) {
							existVal = new HashSet<Object>();
						}
					}
					field.set(conf, existVal);
				}
				
				if(existVal != null && value != null) {
					//ちゃんと存在するのなら、とりあえず値をねじ込む。
					existVal.add(value);
				}
			}
			
			@Override
			public boolean isSplitNeeded() {
				return true;
			}
		};
		return ret;
	}
	
	@Override
	public FieldSetter makeMemberSetter(Class<? extends AnnotatedConfig> clazz, String name, Field field)
			throws AcWrongClassException {
		// TODO Auto-generated method stub
		
		
		Class<?> fieldClazz = field.getType();
		
		if(List.class.isAssignableFrom(fieldClazz) || Set.class.isAssignableFrom(fieldClazz)) {
			//List<E>か、Set<E>であるのならコレクションとしてセットする用意を整える
			return this.makeCollectionSetter(clazz, name, field);
		}
		
		//クラスの検査。プリミティブならラッパークラスに直したうえで実行。（プリミティブのラッパーはVoidを除いて文字列型から変換できる）
		FieldSetter ret = new FieldSetter(field.getType(), field, name) {
			@Override
			public void putValueInside(AnnotatedConfig conf, Member member, String name, Object value, String comment)
					throws Exception {
				// TODO Auto-generated method stub
				Field f = (Field)member;
				System.out.println(name+": single input ["+value.toString()+"]");
				f.set(conf, value);
			}
		};
		return ret;
	}

}
