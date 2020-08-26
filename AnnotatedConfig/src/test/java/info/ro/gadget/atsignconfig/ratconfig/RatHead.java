package info.ro.gadget.atsignconfig.ratconfig;

import info.ro.gadget.atsignconfig.core.AtsignConfig;
import info.ro.gadget.atsignconfig.core.annotation.Constraint;
import info.ro.gadget.atsignconfig.core.annotation.Hidden;
import info.ro.gadget.atsignconfig.core.annotation.TreatFieldsAsParamsImplicitly;
import info.ro.gadget.atsignconfig.core.definition.Restriction;

@TreatFieldsAsParamsImplicitly
@Hidden
public class RatHead extends AtsignConfig{
	
	public RatHead() {
		
	}
	
	@Constraint(Restriction.MUST)
	int TESTINT = -30;
	
	static String STATICAL = "";
	
	
	@Override
	public boolean doFinalValidation() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		return "Head["+this.getReadSection()+"]'s TESTINT: "+this.TESTINT + "\nSTATICAL:"+STATICAL;
	}

}
