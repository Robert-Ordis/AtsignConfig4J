package info.ro.gadget.configurator.core;

import info.ro.gadget.configurator.reader.ConfigReader;

public abstract class AnnotatedConfig {
	//読み込んだセクション。
	//読み込みに使ったリーダー
	//読み込みに使った元ファイル（セクションなど、サブセットの場合は元ファイル。文字列直接の場合はnull）
	String			__section__ 	= null;	//読み込んだセクション（フルパス。未指定の場合はnull）
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
}
