package gred.nucleus.mains;

import java.io.IOException;
import java.util.Properties;

public class Version {
	
	public static String get() {
		
		final Properties properties = new Properties();
		String version = "undefined";
		try {
			properties.load(Version.class.getClassLoader().getResourceAsStream("nucleusj.properties"));
			version = properties.getProperty("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return version;
	}
}
