package info.ro.gadget.configurator.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.ro.gadget.configurator.core.exception.AcWrongClassException;
import info.ro.gadget.configurator.core.exception.AcWrongParamException;
import info.ro.gadget.configurator.core.instance.AcConfigSetterStore;
import info.ro.gadget.configurator.core.instance.deserializer.AcDeserializer;
import info.ro.gadget.configurator.core.instance.deserializer.DeserializerStore;
import info.ro.gadget.configurator.core.instance.setter.MemberSetter;
import info.ro.gadget.configurator.reader.ConfigLineListener;
import info.ro.gadget.configurator.reader.ConfigReader;

public class AnnotatedConfigParser {
	
	Map<Class<? extends AnnotatedConfig>, AcConfigSetterStore> stores;
	DeserializerStore dstore;
	
	public AnnotatedConfigParser() {
		stores = new HashMap<Class<? extends AnnotatedConfig>, AcConfigSetterStore>();
		dstore = new DeserializerStore();
	}
	
	public void registerDeserializer(Class<?> clazz, AcDeserializer des) {
		if(des != null) {
			this.dstore.setDeserializer(clazz, des);
		}
	}
	
	private static <T extends AnnotatedConfig> T  makeInstance(Class<T> clazz) {
		try {
			Constructor<T> con = clazz.getDeclaredConstructor();
			con.setAccessible(true);
			return con.newInstance();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AcWrongClassException("", e.getMessage());
		}
	}
	
	//TODO: とりあえずまずは書く。リロードとかエラー無視とかはいったんおいて置く。
	@SuppressWarnings("unchecked")
	public <T extends AnnotatedConfig> T getConfig(String src, String section, Class<T> clazz, ConfigReader reader) throws AcWrongParamException, IOException{
		//まず、clazzについてリフレクションもりもり書いていく。サブについてももりもり書いていく。
		synchronized(this) {
			if(!stores.containsKey(clazz)) {
				//もし、最初の引数の時点で既に登録済みであったならこの処理は飛ばす。（サブセットについても登録されているだろうから）
				Set<Class<? extends AnnotatedConfig>> subjectClazzSet = new HashSet<Class<? extends AnnotatedConfig>>();
				subjectClazzSet.add(clazz);
				while(subjectClazzSet.size() > 0) {
					Set<Class<? extends AnnotatedConfig>> nextClazzSet = new HashSet<Class<? extends AnnotatedConfig>>();
					for(Class<? extends AnnotatedConfig> c : subjectClazzSet) {
						if(this.stores.containsKey(c)) {
							//一番最初のクラスが登録済みなら飛ばすのと同じ理由。
							continue;
						}
						AcConfigSetterStore s = new AcConfigSetterStore(c, this.dstore);
						stores.put(c, s);
						//storeから受け取った「次にリフレクションを書くべきサブセットクラス」について用意する
						nextClazzSet.addAll(s.getSubClasses());
					}
					subjectClazzSet = nextClazzSet;
				}
			}
		}
		
		//最初のクラスについて、インスタンスを頑張って作成する。
		if(section == null) {
			section = "";
		}
		section = section.trim();
		T retConfig = makeInstance(clazz);
		Set<AnnotatedConfig> readList = new HashSet<AnnotatedConfig>();
		retConfig.setReadSection(section);
		retConfig.setSourcePath(src);
		retConfig.setReader(reader);
		retConfig.setModified(reader.getLastModified(src));
		
		//「これから値をセットしていくインスタンス」のリストを作っておく。
		readList.add(retConfig);
		
		ParserListener listener = new ParserListener();
		AcWrongParamException wrongLog = new AcWrongParamException();
		//作ったインスタンスリストに従い、ひたすら値を読み出していく。値は外のパーサーがやってくれる。
		while(!readList.isEmpty()) {
			//次に読み込むインスタンスのリスト。現在のインスタンスリストがサブセットを持っているなら生成される。
			Set<AnnotatedConfig> nextSet = new HashSet<AnnotatedConfig>();
			for(AnnotatedConfig conf : readList) {
				//とりあえず外注に従って値を読み込んでいく。
				listener.store = this.stores.get(conf.getClass());
				listener.config = conf;
				listener.splitter = reader.getSplitter();
				listener.mustConfig = listener.store.getMustConfiguredMembers();
				
				//実際の値読み込み
				Set<String> subParams = reader.readConfig(src, conf.getReadSection(), listener);
				
				wrongLog.merge(listener.totalExcept);
				for(String mstr : listener.mustConfig.values()) {
					wrongLog.put(conf.getReadSection() + "[" +mstr+ "]", new Exception("MUST CONFIGURED BUT IGNORED."));
				}
				
				//サブセットコンフィグについてインスタンス生成→上が知らせてくれたパラメータ名のものについてのみ値を生成していく。
				//生成したらメンバ変数としてセットし、「次の読み込むサブセット」として登録しておく
				Map<String, Field> subFieldMap = listener.store.getSubsetMap();
				for(String subKey : subFieldMap.keySet()) {
					Field subField = subFieldMap.get(subKey);
					subField.setAccessible(true);
					
					//AnnotatedConfig subConf = makeInstance((Class<? extends AnnotatedConfig>)subField.getType());
					AnnotatedConfig subConf = null;
					try {
						subConf = (AnnotatedConfig)subField.get(conf);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(subConf == null) {
						subConf = makeInstance((Class<? extends AnnotatedConfig>)subField.getType());
					}
					try {
						subField.set(conf, subConf);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(conf.getReadSection().trim().equals("")) {
						subConf.setReadSection(subKey);
					}
					else if(reader.getDomainSeparator() == null){
						break;
					}
					else {
						subConf.setReadSection(conf.getReadSection() + reader.getDomainSeparator() + subKey);
					}
					if(subParams != null && subParams.contains(subKey)) {
						nextSet.add(subConf);
					}
					else {
						for(String mstr : this.stores.get(subConf.getClass()).getMustConfiguredMembers().values()) {
							wrongLog.put(subConf.getReadSection() + "[" +mstr+ "]", new Exception("MUST CONFIGURED BUT IGNORED."));
						}
					}
				}
				
			}
			readList = nextSet;
		}
		wrongLog.throwIfExist();
		System.out.println(wrongLog.getMessage());
		return retConfig;
	}
	
	private class ParserListener implements ConfigLineListener{
		public HashMap<Member, String> mustConfig;
		public AnnotatedConfig config;
		public AcConfigSetterStore store;
		public String splitter = "\\s+";
		public AcWrongParamException totalExcept = new AcWrongParamException();
		
		@Override
		public void onRead(int lineNum, String name, String value, String comment){
			// TODO Auto-generated method stub
			for(MemberSetter setter : store.getMatchedSetters(name)) {
				String[] vals;
				if(setter.isSplitNeeded()) {
					vals = value.split(this.splitter);
				}
				else {
					vals = new String[1];
					vals[0] = value;
				}
				int i = 0;
				for(String val : vals) {
					System.out.println(name+": input ["+val+"]");
					
					//01:PARAM[0]->errorという書式を意識。
					String errKey = lineNum + ":" + name + "[" + i + "]";
					try {
						i++;
						setter.putValue(config, name, val, comment);
					}
					catch(Exception e) {
						e.printStackTrace();
						totalExcept.put(errKey, e);
					}
				}
				mustConfig.remove(setter.getMember());
			}
		}
		
	}
	
}
