package info.ro.gadget.annotatedconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.ro.gadget.annotatedconfig.core.definition.Restriction;

/**
 * 値に対する規制を設定する。各数値/文字列に対してはひとまず上限/下限を設ける。<br>
 * もしその規制から外れた場合には値毎に決まった制裁を与える（与えない場合もある）
 * @author Robert_Ordis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Constraint {
	Restriction value() default Restriction.NORMAL;	//実際の規制。Restrictionの定数からもらう。
	long maxInt() default Long.MAX_VALUE;
	long minInt() default Long.MIN_VALUE;
	double maxDouble() default Double.MAX_VALUE;
	double minDouble() default Double.MIN_VALUE;
	int maxStrLen() default Integer.MAX_VALUE;
	int minStrLen() default 0;
	int maxCollect() default Integer.MAX_VALUE;
	int minCollect() default 0;
}
