package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DefinitionDomain {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(DefinitionDomain.class);

	private DefinitionDomain() {

	}

	private static final String FILE_NAME = "servicecutter/servicecutter-scorer.properties";

	private static Map<String, Double> cache = new HashMap<>();

	/* Priority Scores */
	public static final String PRIORITY_IGNORE = "PRIORITY_IGNORE";
	public static final String PRIORITY_LOW = "PRIORITY_LOW";
	public static final String PRIORITY_NORMAL = "PRIORITY_NORMAL";
	public static final String PRIORITY_RELEVANT = "PRIORITY_RELEVANT";
	public static final String PRIORITY_IMPORTANT = "PRIORITY_IMPORTANT";
	public static final String PRIORITY_VERY_IMPORTANT = "PRIORITY_VERY_IMPORTANT";
	public static final String PRIORITY_CRITICAL = "PRIORITY_CRITICAL";

	/* General Scores */
	public static final String MAX_SCORE = "MAX_SCORE";
	public static final String MIN_SCORE = "MIN_SCORE";
	public static final String NO_SCORE = "NO_SCORE";
	/* Semantic Proximity */
	public static final String SCORE_WRITE = "SCORE_WRITE";
	public static final String SCORE_READ = "SCORE_READ";
	public static final String SCORE_MIXED = "SCORE_MIXED";
	public static final String SCORE_AGGREGATION = "SCORE_AGGREGATION";

	/* Compabilities */
	public static final String CC_4_LOW = "CC_4_LOW";
	public static final String CC_4_NORMAL = "CC_4_NORMAL";
	public static final String CC_4_HIGH = "CC_4_HIGH";

	public static final String CC_6_LOW = "CC_6_LOW";
	public static final String CC_6_NORMAL = "CC_6_NORMAL";
	public static final String CC_6_HIGH = "CC_6_HIGH";

	public static final String CC_7_LOW = "CC_7_LOW";
	public static final String CC_7_NORMAL = "CC_7_NORMAL";
	public static final String CC_7_HIGH = "CC_7_HIGH";

	public static final String CC_8_LOW = "CC_8_LOW";
	public static final String CC_8_NORMAL = "CC_8_NORMAL";
	public static final String CC_8_HIGH = "CC_8_HIGH";

	public static final String CC_10_LOW = "CC_10_LOW";
	public static final String CC_10_NORMAL = "CC_10_NORMAL";
	public static final String CC_10_HIGH = "CC_10_HIGH";

	public static final String CC_13_LOW = "CC_13_LOW";
	public static final String CC_13_NORMAL = "CC_13_NORMAL";
	public static final String CC_13_HIGH = "CC_13_HIGH";

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
