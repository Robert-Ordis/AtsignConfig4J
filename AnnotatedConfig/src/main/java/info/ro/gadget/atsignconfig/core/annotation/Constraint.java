package info.ro.gadget.atsignconfig.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.ro.gadget.atsignconfig.core.definition.Restriction;

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
	long maxInt() default Long.MAX_VALUE;				//整数最大値
	long minInt() default Long.MIN_VALUE;				//整数最小値
	double maxDouble() default Double.MAX_VALUE;		//浮動小数最大値
	double minDouble() default Double.MIN_VALUE;		//浮動小数最小値
	int maxStrLen() default Integer.MAX_VALUE;		//文字列最大長（unicode単位）
	int minStrLen() default 0;							//文字列再象徴（unicode単位）
	int maxCollect() default Integer.MAX_VALUE;		//配列の最大格納数
	int minCollect() default 0;						//配列の最小格納数
}
