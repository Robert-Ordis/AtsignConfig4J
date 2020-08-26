package info.ro.gadget.atsignconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * メンバフィールドがアノテーションだらけになってうざくなってくるクラスにつけるもの<br>
 * これを宣言すると、対象のクラスのフィールドは強制的にパラメータとして取り扱われる。<br>
 * その場合、パラメータ名はフィールド名がそのまま用いられる。<br>
 * もしそうであっては困るフィールドにはNotParamアノテーションをつけると無視される。<br>
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TreatFieldsAsParamsImplicitly {

}
