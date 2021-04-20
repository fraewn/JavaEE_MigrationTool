package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import command.AbstractCommand;

public class Main {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(AbstractCommand.class);

	public static void main(String[] args) {
		String javaClassPath = System.getProperty("java.class.path");
		if (javaClassPath != null) {
			for (String path : javaClassPath.split(File.pathSeparator)) {
				LOG.info("Loaded jar: " + path);
			}
		}
		String propertiesFile = "migrationtool.properties";
		List<String> arguments = new ArrayList<>(Arrays.asList(args));
		for (String arg : arguments) {
			if (arg.startsWith("-propertiesFile") && arg.contains("=")) {
				propertiesFile = arg.split("=")[1];
				arguments.remove(arg);
				break;
			}
		}
		try (InputStream input = new FileInputStream(propertiesFile)) {
			Properties prop = new Properties();
			prop.load(input);
			for (Map.Entry<Object, Object> e : prop.entrySet()) {
				String key = e.getKey().toString();
				key = key.startsWith("-") ? key : "-" + key;
				String tmp = key + "=" + e.getValue().toString();
				arguments.add(tmp);
			}
		} catch (IOException ex) {
			LOG.info("No properties file found");
		}
		Runner runner = new Runner();
		runner.run(arguments.toArray(new String[0]));
	}
}
