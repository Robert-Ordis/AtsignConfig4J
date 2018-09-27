package info.ro.gadget.configurator.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * コンフィグ中にサブセットが存在した場合、そのサブセットを特定の名前（＝ドメイン）にて<br>
 * 指定しておくことで読み込めるようにするためのアノテーション
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigSubset {
	String	value() default "";		//対象とするパラメータ名。デフォルトの空白ではメンバフィールド名そのまま。
}
