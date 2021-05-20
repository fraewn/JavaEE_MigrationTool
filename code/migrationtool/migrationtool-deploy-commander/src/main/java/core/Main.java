package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import utils.PropertiesLoader;

public class Main {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		// Print logo
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("ascii-art.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOG.info("Starting migrationtool (version: commandline)");
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
		PropertiesLoader properties = new PropertiesLoader(propertiesFile);
		properties.loadProps();
		for (Map.Entry<String, String> e : properties.getCache().entrySet()) {
			String key = e.getKey();
			key = key.startsWith("-") ? key : "-" + key;
			String value = e.getValue();
			value = value.equals("true") ? "" : "=" + e.getValue();
			// remove false arguments
			if (!value.equals("false")) {
				arguments.add(key + value);
			}
		}
		Runner runner = new Runner();
		runner.run(arguments.toArray(new String[0]));
	}
}
