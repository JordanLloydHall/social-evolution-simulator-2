package worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReader {

	public static Properties getProperties() throws IOException {
		Properties prop = new Properties();
		FileInputStream ip = new FileInputStream("./config.properties");
		prop.load(ip);
		return prop;
	}
}
