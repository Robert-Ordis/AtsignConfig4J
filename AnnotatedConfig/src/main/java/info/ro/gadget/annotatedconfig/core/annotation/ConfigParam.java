package info.ro.gadget.annotatedconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.ro.gadget.annotatedconfig.core.definition.FieldMethod;

/**
 * メンバに対して「コンフィグ対象である」ことを宣言するアノテーション<br>
 * 名前を指定しない場合はメンバ名がそのままコンフィグ名となる。<br>
 * 基本的にはString一つで生成できる値（そういうコンストラクタを持ってるもの）を対象とするが、<br>
 * デシリアライザを定義することでそうではないクラスも対象にできる。<br>
 * 
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigParam {
	String[]	value()		default {""};						//コンフィグ名。何もないならメンバ名がそのままコンフィグ名となる
	FieldMethod	method()	default FieldMethod.SINGLE;		//メソッド。デフォルトはSINGLEとする。
}
