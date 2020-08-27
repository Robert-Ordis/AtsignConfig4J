package info.ro.gadget.atsignconfig.reader;

/**
 * Publisher for each events from config file.
 * * Concrete instance will be provided from library.
 * @author Robert_Ordis
 * @remarks	This is for the programmer who try to make an external parser.
 */
public interface AtsignConfigLineListener {
	/**
	 * Input value with written name.
	 * @param lineNo	Line number in the source.
	 * @param name		Parameter name in the source. Indicated with relative level.
	 * @param value		Value written in the source. Pass it "as is" as you can.
	 * @param comment	Comment in the source.
	 */
	public void onValue(int lineNo, String name, String value, String comment);
	
	/**
	 * Notify the parser that the config level becomes one more deeper.
	 * @param lineNo	Line number in the source.
	 * @param name		Parameter name in the source. Indicated with relative level.
	 * @param comment	Comment in the source.
	 * 
	 * @remarks		This is for a hierarchical configuration, e.g. json, xml, properties with period, yaml...
	 * @remarks		For one liner (e.g. level1.level2.level3 in properteis), call this in each level.
	 */
	public void pushSubset(int lineNo, String name, String comment);
	
	/**
	 * Notify the parser that the config level becomes one more upper.
	 * @param lineNo
	 * 
	 * @remarks		This is for a hierarchical and structural format(e.g. json, xml, yaml)
	 */
	public void popSubset(int lineNo);
	
	/**
	 * Notify the parser to reset the level of config to initial value..
	 */
	public void clearSubset();
	
	/**
	 * Notify the parser to switch the section.
	 * @param lineNo	Line number that the section is written.
	 * @param name		Section name.
	 * @param comment	Comment in the source.
	 */
	public void switchSection(int lineNo, String name, String comment);

}
