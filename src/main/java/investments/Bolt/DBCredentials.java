/**
 * 
 */
package investments.Bolt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;


/**
 * @author dimitri
 *
 */
class DBCredentials {
	// static Class variables
	// private Instance variable
	private String mySqlUrlConnection;
	// Initialiser block
	// Constructors
	DBCredentials(File iniFile) throws InvalidFileFormatException, IOException {
		// TODO нужно использовать .properties https://metanit.com/java/database/1.1.php
		Wini ini = new Wini(iniFile);
		this.mySqlUrlConnection = ini.get("localTestDB", "mySqlUrlConnection");
	}

	// Methods

	// Mutator (= setter) methods
	// Accessor (= getter) methods
	String getInstanceVariable() {
		return "this.InstanceVariable";
	}

	/**
	 * @return the mySqlUrlConnection
	 */
	public String getMySqlUrlConnection() {
		return this.mySqlUrlConnection;
	}
}
