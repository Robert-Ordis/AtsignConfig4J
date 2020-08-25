package info.ro.gadget.annotatedconfig.core;

import java.io.IOException;
import java.sql.Timestamp;

import info.ro.gadget.annotatedconfig.reader.ConfigReader;

public abstract class AnnotatedConfig {
	//読み込んだセクション。
	//読み込みに使ったリーダー
	//読み込みに使った元ファイル（セクションなど、サブセットの場合は元ファイル。文字列直接の場合はnull）
	String			__section__ 	= null;	//読み込んだセクション（フルパス。未指定の場合はnull）
	String			__src__			= null;	//読み込みに使った元ファイル（サブセットなど、文字列を直接読んだ場合はnull）
	ConfigReader	__reader__		= null;	//このコンフィグを読み込むのに使ったリーダ
	Timestamp		__modified__	= null;	//対象の更新日時
	
	/**
	 * 値単体では実行できない複合的なチェックを行うためのメソッド。
	 * @return
	 */
	public abstract boolean doFinalValidation();
	public final String getReadSection() {
		return this.__section__;
	}
	public final void setReadSection(String arg) {
		this.__section__ = arg;
	}
	public final String getSourcePath() {
		return this.__src__;
	}
	public final void setSourcePath(String arg) {
		this.__src__ = arg;
	}
	public final ConfigReader getReader() {
		return this.__reader__;
	}
	public final void setReader(ConfigReader arg) {
		this.__reader__ = arg;
	}
	public final void setModified(Timestamp arg) {
		this.__modified__ = arg;
	}
	public final boolean isModified() throws IOException {
		if(this.__modified__ == null) {
			return true;
		}
		Timestamp tmp = this.__reader__.getLastModified(this.__src__);
		if(tmp == null) {
			return true;
		}
		//最終更新日時が変わっていない場合にfalse。
		return !tmp.equals(this.__modified__);
	}
}
