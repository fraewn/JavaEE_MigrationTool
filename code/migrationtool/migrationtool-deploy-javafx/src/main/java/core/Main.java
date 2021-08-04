package core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Appl;
import javafx.application.Application;
import utils.PropertiesLoader;

public class Main {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) {
		LOG.info("Starting migrationtool (version: javafx)");
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
		properties.loadProps(true);
		for (Map.Entry<String, String> e : properties.getCache().entrySet()) {
			String key = e.getKey();
			key = key.startsWith("-") ? key : "-" + key;
			String value = e.getValue();
			arguments.add(!value.equals("true") ? key + "=" + value : key);
		}
		if (arguments.contains("exec")) {
			arguments.remove("exec");
			Runner runner = new Runner();
			runner.run(arguments.toArray(new String[0]));
		} else {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> {
				Application.launch(Appl.class, arguments.toArray(new String[0]));
			});
		}
	}
}
