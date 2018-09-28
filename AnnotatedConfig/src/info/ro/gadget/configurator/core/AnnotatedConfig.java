package info.ro.gadget.configurator.core;

import info.ro.gadget.configurator.reader.ConfigReader;

public abstract class AnnotatedConfig {
	//読み込んだセクション。
	//読み込みに使ったリーダー
	//読み込みに使った元ファイル（セクションなど、サブセットの場合は元ファイル。文字列直接の場合はnull）
	String			__section__ 	= null;	//読み込んだセクション（フルパス。未指定の場合はnull）
	String[]		__domains__		= null;	//ドメイン指定。
	String			__src__			= null;	//読み込みに使った元ファイル（サブセットなど、文字列を直接読んだ場合はnull）
	ConfigReader	__reader__		= null;	//このコンフィグを読み込むのに使ったリーダ
	
	/**
	 * 値単体では実行できない複合的なチェックを行うためのメソッド。
	 * @return
	 */
	public abstract boolean doFinalValidation();
	public String getReadSection() {
		return this.__section__;
	}
	public void setReadSection(String arg) {
		this.__section__ = arg;
	}
	public String getSourcePath() {
		return this.__src__;
	}
	public void setSourcePath(String arg) {
		this.__src__ = arg;
	}
	public ConfigReader getReader() {
		return this.__reader__;
	}
	public void setReader(ConfigReader arg) {
		this.__reader__ = arg;
	}
	public String getDomainString(String splitter) {
		if(this.__domains__ == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(this.__domains__[0]);
		for(int i = 1; i < this.__domains__.length; i++) {
			if(!this.__domains__[i-1].equals("")) {
				sb.append(splitter);
			}
			sb.append(this.__domains__[i]);
		}
		return sb.toString();
	}
	
	public void setDomainPath(AnnotatedConfig parent, String mine) {
		int parentLen = 0;
		if(parent != null && parent.__domains__ != null) {
			parentLen = parent.__domains__.length;
		}
		this.__domains__ = new String[parentLen];
		for(int i = 0; i < parentLen; i++) {
			this.__domains__[i] = parent.__domains__[i];
		}
		this.__domains__[parentLen] = mine;
	}
}
