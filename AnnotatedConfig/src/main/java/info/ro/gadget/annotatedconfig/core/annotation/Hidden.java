package info.ro.gadget.annotatedconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 「ログにパラメータ詳細を出してほしくない」と宣言するアノテーション<br>
 * さすがに、MethodやRegex指定の関数で書かれることまでは干渉できないですよ<br>
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Hidden {

}
