package resolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Scores {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Scores.class);

	private Scores() {

	}

	private static final String FILE_NAME = "servicecutter/servicecutter-scorer.properties";

	private static Map<String, Double> cache = new HashMap<>();

	public static final String MAX_SCORE = "MAX_SCORE";
	public static final String MIN_SCORE = "MIN_SCORE";
	public static final String NO_SCORE = "NO_SCORE";

	public static double getScore(String key) {
		if (cache.isEmpty()) {
			loadScores();
		}
		return cache.get(key);
	}

	private static void loadScores() {
		try (InputStream input = new FileInputStream(FILE_NAME)) {
			Properties prop = new Properties();
			prop.load(input);
			for (Map.Entry<Object, Object> e : prop.entrySet()) {
				cache.put(e.getKey().toString(), Double.parseDouble(e.getValue().toString()));
			}
		} catch (IOException ex) {
			LOG.error("No properties file found", ex);
		} catch (NumberFormatException e) {
			LOG.error("Not a valid number in properties file: " + FILE_NAME);
		}
	}
}
