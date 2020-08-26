package info.ro.gadget.atsignconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 「このメンバはコンフィグパラメータではないですよ」と宣言するアノテーション<br>
 * TreatFieldsAsParamsImplicitlyと併用します。<br>
 * 仮にConfigParamと一緒に宣言した場合、こちらが優先されます<br>
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NotParam {

}
