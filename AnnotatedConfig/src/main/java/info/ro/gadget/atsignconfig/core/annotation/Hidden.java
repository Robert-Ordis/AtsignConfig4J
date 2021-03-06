package info.ro.gadget.atsignconfig.core.annotation;

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
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Hidden {

}
