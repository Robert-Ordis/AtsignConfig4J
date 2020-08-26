package info.ro.gadget.atsignconfig.core.instance.setter;

import java.lang.reflect.Member;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.annotation.Constraint;

public interface MemberSetter {
	public void setConstraint(Constraint c);
	public void putValueInside(AtsignConfig conf, Member member, String name, Object value, String comment) throws Exception;
	public void putValue(AtsignConfig conf, String name, String value, String comment) throws Exception;
	public String getName();
	public boolean isSplitNeeded();
	public Member getMember();
	public boolean isHidden();
}
