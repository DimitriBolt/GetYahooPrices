package investments.Bolt.allTickersAggregator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

class DB_Credentials {

	// static Class variables
	// private Instance variable
	private String property;
	private Properties props;

	// Initializer block
	{
		props = new Properties();
	}

	// Constructors
	DB_Credentials() {

		String userHomePath = System.getProperty("user.home");
		Path path = Paths.get(userHomePath, "Documents", "OracleCredentials.properties");
		try (InputStream in = Files.newInputStream(path)) {
			//Суть. 
			this.props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	String getProperty(String key) {
		this.property = this.props.getProperty(key);
		return property;
	}
}
