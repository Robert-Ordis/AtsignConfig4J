package info.ro.gadget.atsignconfig.reader;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Config source reader.
 * Actual file reading is this role.
 * 
 * It can be implemented also to read from System property, Environment variable.
 * @author Robert_Ordis
 *
 */
public interface AtsignConfigReader {
	
	/**
	 * Split written value into the collection of values according to your rule.
	 * @param value
	 * @return
	 * 
	 * @remarks	This will be called only in the property defined as Collection.(e.g. List, Set)
	 */
	public String[] explodeValue(String value);
	
	/**
	 * Read actual config source in this method.
	 * @param path			Ordinary, The path for config file. In File-less case, this may be empty.
	 * @param listener		Event listener for config source. The simplest role is just inputing values.
	 * @throws IOException	Throw if file not found or something else.
	 */
	public void readConfig(String path, AtsignConfigLineListener listener) throws IOException;
	
	/**
	 * Get the timestamp of specified config file.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public Timestamp	getLastModified(String path) throws IOException;
	
}
