package TestRatPackage;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import info.ro.gadget.configurator.core.AnnotatedConfig;
import info.ro.gadget.configurator.core.annotation.ConfigFunc;
import info.ro.gadget.configurator.core.annotation.ConfigParam;
import info.ro.gadget.configurator.core.annotation.ConfigSubset;
import info.ro.gadget.configurator.core.annotation.Constraint;
import info.ro.gadget.configurator.core.annotation.NotParam;
import info.ro.gadget.configurator.core.annotation.TreatFieldsAsParamsImplicitly;
import info.ro.gadget.configurator.core.definition.FieldMethod;
import info.ro.gadget.configurator.core.definition.FuncMethod;
import info.ro.gadget.configurator.core.definition.Restriction;

@TreatFieldsAsParamsImplicitly
public class LaboRat extends AnnotatedConfig{
	public LaboRat() {
		
	}
	
	@Constraint(Restriction.VALID)
	private int INTEGER;
	
	@ConfigParam({"", "LONG"})
	@Constraint(Restriction.VALID)
	private long INTEGER_L = -1;
	
	@Constraint(value = Restriction.VALID, maxDouble = 50.0)
	private float FLOATING = -1;
	
	private double FLOATING_D = -1;
	
	private boolean ARG_BOOL = false;
	
	@ConfigParam(method = FieldMethod.ARGLESS)
	private boolean ARGLESS_BOOL = false;
	
	private char[] STR;
	
	@Constraint(Restriction.VALID)
	private List<Integer> INTLIST;
	
	private Set<Double> DOUBLESET;
	
	private List<RatName> NAMED_LIST;
	
	@Constraint(Restriction.VALID)
	private RatName NAME;
	
	@Constraint(Restriction.VALID)
	private InetAddress net;
	
	@ConfigSubset("head b")
	private RatHead head;
	
	@ConfigSubset
	private RatHead headSecond;
	
	@Constraint(Restriction.VALID)
	private AbstPart part;
	
	@NotParam
	int	setterCount = 0;
	@NotParam
	int regexCount = 0;
	@NotParam
	private List<String> setterHistory = new ArrayList<String>();
	
	@ConfigFunc("SETTER1")
	void successOneSetter(String value) {
		this.setterHistory.add(setterCount+":"+value);
		setterCount++;
	}
	
	@ConfigFunc("SETTER2")
	void successTwoSetter(String value, String comment) {
		this.setterHistory.add(setterCount+":"+value+"/"+comment);
		setterCount++;
	}
	
	@ConfigFunc(value = "REGEX_ONE(\\d+)", method = FuncMethod.REGEX)
	void successOneRegex(Matcher m, String value) {
		this.setterHistory.add("reg"+regexCount+":"+value+"["+m.group(1)+"]");
		regexCount++;
	}
	
	@ConfigFunc(value = "^REGEX_TWO(\\d+)$", method = FuncMethod.REGEX)
	void successTwoRegex(Matcher m, String value, String comment) {
		System.out.println("regextwo:"+m);
		this.setterHistory.add("reg"+regexCount+":"+value+"/"+comment+"["+m.group(1)+"]");
		regexCount++;
	}
	
	@ConfigFunc(value = "AUTODIAL(\\d+)(\\+?)", method = FuncMethod.REGEX)
	@Constraint(Restriction.MUST)
	void setAutoDialer(Matcher m, String value, String comment) throws Exception{
		System.out.println("autodial:"+m);
		System.out.println("comment[AUTODIAL]:"+comment);
	}
	
	@ConfigFunc("headSecond.additional")
	void setDotParamTest(String value, String comment) {
		System.out.println("headSecond.additional:"+value+";"+comment);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("["+this.getReadSection()+"]\n");
		sb.append("integer:"+INTEGER);
		sb.append("\n");
		sb.append("integerL:"+INTEGER_L);
		sb.append("\n");
		sb.append("floating:"+FLOATING);
		sb.append("\n");
		sb.append("floatingD:"+FLOATING_D);
		sb.append("\n");
		sb.append("str:"+new String(STR != null ? STR:new char[0]));
		sb.append("\n");
		sb.append("argBool:"+ARG_BOOL);
		sb.append("\n");
		sb.append("arglessBool:"+ARGLESS_BOOL);
		sb.append("\n");
		sb.append("intList:[");
		if(INTLIST != null) {
			for(int i:INTLIST) {
				sb.append(i+",");
			}
		}
		sb.append("]\n");
		sb.append("doubleSet:[");
		if(DOUBLESET != null) {
			for(double i:DOUBLESET) {
				sb.append(i+",");
			}
		}
		sb.append("]\n");
		sb.append("namedList:[");
		if(NAMED_LIST != null) {
			for(RatName i:NAMED_LIST) {
				sb.append(i+",");
			}
		}
		sb.append("]\n");
		sb.append("setterHistory:[");
		if(setterHistory != null) {
			for(String i:setterHistory) {
				sb.append(i+",");
			}
		}
		sb.append("]\n");
		sb.append(NAME == null ? "I haven't took my name yet.":NAME.toString());
		if(this.head != null) {
			sb.append("\nhead:");
			sb.append(this.head.toString());
		}
		if(this.headSecond != null) {
			sb.append("\nheadSecond:");
			sb.append(this.headSecond.toString());
		}
		if(this.net != null) {
			sb.append("\nnet:");
			sb.append(this.net.getHostAddress());
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public boolean doFinalValidation() {
		// TODO Auto-generated method stub
		return true;
	}
}
