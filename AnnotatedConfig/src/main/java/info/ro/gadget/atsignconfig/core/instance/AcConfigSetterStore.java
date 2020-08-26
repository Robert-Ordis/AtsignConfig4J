package info.ro.gadget.atsignconfig.core.instance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.annotation.ConfigFunc;
import info.ro.gadget.atsignconfig.core.annotation.ConfigParam;
import info.ro.gadget.atsignconfig.core.annotation.ConfigSubset;
import info.ro.gadget.atsignconfig.core.annotation.Constraint;
import info.ro.gadget.atsignconfig.core.annotation.NotParam;
import info.ro.gadget.atsignconfig.core.annotation.TreatFieldsAsParamsImplicitly;
import info.ro.gadget.atsignconfig.core.definition.FieldMethod;
import info.ro.gadget.atsignconfig.core.definition.member.AcMethod;
import info.ro.gadget.atsignconfig.core.exception.AcWrongClassException;
import info.ro.gadget.atsignconfig.core.instance.deserializer.DeserializerStore;
import info.ro.gadget.atsignconfig.core.instance.setter.FieldSetter;
import info.ro.gadget.atsignconfig.core.instance.setter.MemberSetter;
import info.ro.gadget.atsignconfig.core.instance.setter.MethodSetter;

public class AcConfigSetterStore {
	
	private static final ConfigParam defaultParamAnnotation = new ConfigParam() {
		String[] v = {""};
		@Override
		public Class<? extends Annotation> annotationType() {return ConfigParam.class;}
		@Override
		public String[] value() { return v;}
		@Override
		public FieldMethod method() {return FieldMethod.SINGLE;}
	};
	
	Map<String, MemberSetter> simpleSetterMap;
	Map<String, MemberSetter> regexSetterMap;
	Map<String, Field> subsetMap;
	Set<Class<? extends AtsignConfig>> subsetClasses;
	Set<MemberSetter> mustConfigured;
	String terminator = null;
	
	private void throwIfDuplicated(String name, String keyInError) throws AcWrongClassException{
		if(this.simpleSetterMap.containsKey(name) || this.subsetMap.containsKey(name) || this.regexSetterMap.containsKey(name) || name.equals(this.terminator)) {
			throw new AcWrongClassException(keyInError, "Duplicated key.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public AcConfigSetterStore(Class<? extends AtsignConfig> moldClazz, DeserializerStore dstores) throws AcWrongClassException{
		System.out.println("moldClass is "+moldClazz);
		
		this.simpleSetterMap = new HashMap<String, MemberSetter>();
		this.regexSetterMap = new HashMap<String, MemberSetter>();
		this.subsetMap = new HashMap<String, Field>();
		this.subsetClasses = new HashSet<Class<? extends AtsignConfig>>();
		this.mustConfigured = new HashSet<MemberSetter>();
		boolean implicitParam = false;	//暗黙的にフィールドをパラメータとして扱うかどうか
		
		TreatFieldsAsParamsImplicitly implAnnotation = moldClazz.getAnnotation(TreatFieldsAsParamsImplicitly.class);
		implicitParam = implAnnotation != null;
		AcWrongClassException errorMerger = null;
		for(Field field : moldClazz.getDeclaredFields()) {
			//まずは基幹となるConfigParamアノテーションを取得する。
			System.out.println(field.getType() + " " + field.getName());
			if(field.getAnnotation(NotParam.class) != null) {
				//@NotParam-> this is NOT a parameter.
				continue;
			}
			try {
				ConfigParam paramAnnotation = field.getAnnotation(ConfigParam.class);
				ConfigSubset subsetAnnotation = field.getAnnotation(ConfigSubset.class);
				FieldSetter setter;
				if(paramAnnotation != null && subsetAnnotation != null ) {
					//両方はつけられないので注意。
					throw new AcWrongClassException(field.getName(), "Cannot attach both of these annotations:ConfigParam & ConfigSubset.");
				}
				if(subsetAnnotation != null) {
					//フィールドが仮にSubsetConfigだった場合。必ずAnnotatedConfigでなければならない。
					System.out.println(subsetAnnotation);
					String pName = subsetAnnotation.value().trim();
					if(pName.equals("")) {
						pName = field.getName();
					}
					String key = moldClazz.getName() + "[" + pName + "]";
					this.throwIfDuplicated(pName, key);
					Class<?> subClazz = field.getType();
					if(!AtsignConfig.class.isAssignableFrom(subClazz)) {
						throw new AcWrongClassException(key, "Must be sub-class of AnnotatedConfig");
					}
					//外注するための材料としてマップに置いておく。一応サブクラスとしておいておけることは確認してある、つもり。
					this.subsetMap.put(pName, field);
					this.subsetClasses.add((Class<? extends AtsignConfig>)subClazz);
					continue;
				}
				if(paramAnnotation == null && implicitParam) {
					paramAnnotation = defaultParamAnnotation;
				}
				if(paramAnnotation != null) {
					//フツーのコンフィグとして置いておく
					System.out.println(paramAnnotation);
					List<String> params = Arrays.asList(paramAnnotation.value());
					if(params.size() == 0) {
						params.add("");
					}
					for(String pName : params) {
						pName = pName.trim();
						if(pName.equals("")) {
							pName = field.getName();
						}
						String key = moldClazz.getName() + "[" + pName + "]";
						this.throwIfDuplicated(pName, key);
						
						FieldMethod fm = paramAnnotation.method();
						setter = fm.getMethod().makeMemberSetter(moldClazz, pName, field);
						Constraint c = field.getAnnotation(Constraint.class);
						setter.setConstraint(c);
						try {
							setter.setDeserializer(dstores.getDeserializer(setter.getMemberClass()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new AcWrongClassException(key, e.getMessage());
						}
						//「必ず設定しなければならない」制約に対してつけられる。
						if(c != null && c.value().isRequired()) {
							this.mustConfigured.add(setter);
						}
						//アノテーションとかいろいろと設定した上でセッター一覧に登録する。
						this.simpleSetterMap.put(pName, setter);
					}
					
				}
			}
			catch(AcWrongClassException e) {
				if(errorMerger == null) {
					errorMerger = e;
				}
				else {
					errorMerger.merge(e);
				}
			}
		}
		
		for(Method method:moldClazz.getDeclaredMethods()) {
			System.out.println(method.getName());
			try {
				//ConfigFuncアノテーションで入れられるパラメータ
				ConfigFunc funcAnnotation = method.getAnnotation(ConfigFunc.class);
				if(funcAnnotation == null) {
					continue;
				}
				System.out.println(funcAnnotation);
				//ここではパラメータ名は必ず設定する
				for(String pName : funcAnnotation.value()) {
					pName = pName.trim();
					String key = moldClazz.getName() + "[" + pName + "]";
					if(pName.equals("")) {
						throw new AcWrongClassException(method.getName(), "ConfigFunc must have any ACTUAL parameter name.");
					}
					this.throwIfDuplicated(pName, key);
					
					//関数に関するあれこれなセッターを生成する。
					AcMethod am = funcAnnotation.method().getMethod();
					MethodSetter setter = am.makeMemberSetter(moldClazz, pName, method);
					Constraint c = method.getAnnotation(Constraint.class);
					setter.setConstraint(c);
					//「必ず設定しなければならない」制約に対してつけられる。
					if(c != null && c.value().isRequired()) {
						this.mustConfigured.add(setter);
					}
					if(am == AcMethod.REGEX) {
						//正規表現用だった場合。単純なマップではセッターをとれないので専用のマップに割り当てる。
						this.regexSetterMap.put(pName, setter);
					}
					else {
						this.simpleSetterMap.put(pName, setter);
					}
				}
				
			}
			catch(AcWrongClassException e) {
				if(errorMerger == null) {
					errorMerger = e;
				}
				else {
					errorMerger.merge(e);
				}
			}
		}
		if(errorMerger != null) {
			throw errorMerger;
		}
	}
	
	/**
	 * 「必ず設定しなければならない」項目についてのメンバ群を取得する。<br>
	 * 一つのメンバにつき複数の項目名を設定できるため、この辺りの制約はメンバ毎で判定する<br>
	 * @return
	 */
	public HashMap<Member, String> getMustConfiguredMembers(){
		HashMap<Member, String> ret = new HashMap<Member, String>();
		for(MemberSetter m : this.mustConfigured) {
			ret.put(m.getMember(), m.getName());
		}
		return ret;
	}
	
	/**
	 * 指定したパラメータ名に一致するセッターを取得する
	 * @param pName
	 * @return
	 */
	public Set<MemberSetter> getMatchedSetters(String pName){
		Set<MemberSetter> ret = new HashSet<MemberSetter>();
		if(this.simpleSetterMap.containsKey(pName)) {
			ret.add(this.simpleSetterMap.get(pName));
		}
		for(String key : this.regexSetterMap.keySet()) {
			if(Pattern.matches(key, pName)) {
				ret.add(this.regexSetterMap.get(key));
			}
		}
		return ret;
	}
	
	public Set<Class<? extends AtsignConfig>> getSubClasses(){
		return this.subsetClasses;
	}
	
	public Map<String, Field> getSubsetMap(){
		return this.subsetMap;
	}
	
}
