package info.ro.gadget.atsignconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.ro.gadget.atsignconfig.core.definition.FuncMethod;

/**
 * 指定したパラメータ名から結びつくセッター関数であることを宣言するアノテーション<br>
 * 少なくとも名前は指定すること。（正規表現メソッドとの兼ね合いがあるから）<br>
 * 未指定の場合はセッター関数とはみなさない。<br>
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ConfigFunc {
	String[]	value()		;								//コンフィグ名。絶対に値を付けること。
	FuncMethod	method()	default FuncMethod.SETTER;		//メソッド。デフォルトはSETTERとする。
}
