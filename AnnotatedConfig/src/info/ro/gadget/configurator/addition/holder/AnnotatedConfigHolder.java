package info.ro.gadget.configurator.addition.holder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.AnnotatedConfigParser;
import info.ro.gadget.configurator.core.exception.AcWrongParamException;
import info.ro.gadget.configurator.reader.ConfigReader;

/**
 * コンフィグを読み込みつつ、それを複数保管して使いまわそうともくろむためのクラス<br>
 * 昔、同じ型の設定を場合によって異なるファイルから読んで運用することがあったので<br>
 * それ用にそれっぽいことをした時の産物（その時はシングルトンでした）<br>
 * @author Robert_Ordis
 *
 */
public class AnnotatedConfigHolder {
	
	Map<String, Map<Class<? extends AnnotatedConfig>, AnnotatedConfig>> configMap
		=new HashMap<String, Map<Class<? extends AnnotatedConfig>, AnnotatedConfig>>();
	
	AnnotatedConfigParser parser = new AnnotatedConfigParser();
	
	public AnnotatedConfigHolder() {
		
	}
	
	@SuppressWarnings("unchecked")
	private <T extends AnnotatedConfig> T pickConfig(String key, Class<T> clazz){
		if(clazz == null){
			return null;
		}
		Map<Class<? extends AnnotatedConfig>, AnnotatedConfig> tmpMap = this.configMap.get(key);
		if(tmpMap == null){
			return null;
		}
		return (T)tmpMap.get(clazz);
	}
	
	private <T extends AnnotatedConfig> void putConfig(String key, Class<T> clazz, T conf) {
		Map<Class<? extends AnnotatedConfig>, AnnotatedConfig> tmpMap = this.configMap.get(key);
		if(tmpMap == null) {
			tmpMap = new HashMap<Class<? extends AnnotatedConfig>, AnnotatedConfig>();
			this.configMap.put(key, tmpMap);
		}
		if(conf != null) {
			tmpMap.put(conf.getClass(), conf);
		}
	}
	
	/**
	 * 備え付けたパーサを取得する。<br>
	 * 主にパーサに特定のオブジェクト用のデシリアライザを登録するため<br>
	 * @return
	 */
	public AnnotatedConfigParser getParser() {
		return this.parser;
	}
	
	/**
	 * 指定したキーとクラスにて、コンフィグを読み込ませて登録する。
	 * @param key
	 * @param clazz
	 * @param src
	 * @param section
	 * @param reader
	 * @return
	 * @throws AcWrongParamException
	 * @throws IOException
	 */
	public <T extends AnnotatedConfig> T 
		registerConfig(String key, Class<T> clazz, String src, String section, ConfigReader reader) 
				throws AcWrongParamException, IOException{
		//readerとsrcとclazz使ってコンフィグ読み込む
		//読み込んだらkeyに入れる
		T tmpConfig = this.parser.getConfig(src, section, clazz, reader);
		this.putConfig(key, clazz, tmpConfig);
		return tmpConfig;
	}
	
	/**
	 * 指定したキーとクラスの読み込み済みコンフィグを取得する。
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T extends AnnotatedConfig> T getConfig(String key, Class<T> clazz) {
		return this.pickConfig(key, clazz);
	}
	
	/**
	 * 指定したキーとクラスのコンフィグを再読み込みさせる。
	 * 読み込まれていない場合は無視される。
	 * @param key
	 * @param clazz
	 * @throws IOException 
	 * @throws AcWrongParamException 
	 */
	public <T extends AnnotatedConfig> T reloadConfig(String key, Class<T> clazz) throws AcWrongParamException, IOException {
		T conf = this.pickConfig(key, clazz);
		if(conf == null) {
			return null;
		}
		T newConf = parser.getConfig(conf.getSourcePath(), conf.getReadSection(), clazz, conf.getReader());
		this.putConfig(key, clazz, newConf);
		return newConf;
	}
	
	/**
	 * 指定された種類のコンフィグをすべて取得する。
	 * @param clazz
	 * @param reload
	 * @return
	 */
	public synchronized <T extends AnnotatedConfig> Map<String, T> getSpecifiedConfigs(Class<T> clazz, boolean reload){
		Map<String, T> retMap = new HashMap<String, T>();
		for(String key : this.configMap.keySet()) {
			T conf = this.pickConfig(key, clazz);
			if(conf != null) {
				retMap.put(key, conf);
			}
		}
		
		if(reload) {
			for(String key : new HashSet<String>(retMap.keySet())) {
				try {
					T conf = this.reloadConfig(key, clazz);
					retMap.put(key, conf);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return retMap;
	}
	
	/**
	 * 指定されたキーのコンフィグを全部読み直す。
	 * @param key
	 */
	public synchronized void reloadConfigsOnKey(String key) {
		Map<Class<? extends AnnotatedConfig>, AnnotatedConfig> newMap = new HashMap<Class<? extends AnnotatedConfig>, AnnotatedConfig>();
		if(this.configMap.containsKey(key)) {
			for(Entry<Class<? extends AnnotatedConfig>, AnnotatedConfig> entry : this.configMap.get(key).entrySet()) {
				AnnotatedConfig conf = entry.getValue();
				if(conf != null) {
					try {
						conf = this.parser.getConfig(conf.getSourcePath(), conf.getReadSection(), entry.getKey(), conf.getReader());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(conf != null) {
					newMap.put(entry.getKey(), conf);
				}
			}
		}
		if(newMap.size() > 0) {
			this.configMap.put(key, newMap);
		}
	}
	
}
