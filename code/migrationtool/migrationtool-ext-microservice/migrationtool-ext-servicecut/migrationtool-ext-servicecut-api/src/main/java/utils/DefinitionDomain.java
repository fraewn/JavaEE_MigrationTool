package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolRuntimeException;

/**
 * Loads the configuration values used by the algorithmn
 */
public class DefinitionDomain {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Location of configurations file */
	public static final String FILE_NAME = "servicesnipper/servicesnipper-scorer.properties";
	/** Properties loader */
	private static PropertiesLoader loader = new PropertiesLoader(FILE_NAME);

	private DefinitionDomain() {

	}

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
	public static final String CC_9_LOW = "CC_9_LOW";
	public static final String CC_9_NORMAL = "CC_9_NORMAL";
	public static final String CC_9_HIGH = "CC_9_HIGH";

	public static final String CC_10_LOW = "CC_10_LOW";
	public static final String CC_10_NORMAL = "CC_10_NORMAL";
	public static final String CC_10_HIGH = "CC_10_HIGH";

	public static final String CC_11_LOW = "CC_11_LOW";
	public static final String CC_11_NORMAL = "CC_11_NORMAL";
	public static final String CC_11_HIGH = "CC_11_HIGH";

	public static final String CC_12_LOW = "CC_12_LOW";
	public static final String CC_12_NORMAL = "CC_12_NORMAL";
	public static final String CC_12_HIGH = "CC_12_HIGH";

	public static final String CC_13_LOW = "CC_13_LOW";
	public static final String CC_13_NORMAL = "CC_13_NORMAL";
	public static final String CC_13_HIGH = "CC_13_HIGH";

	public static final String CC_14_LOW = "CC_14_LOW";
	public static final String CC_14_NORMAL = "CC_14_NORMAL";
	public static final String CC_14_HIGH = "CC_14_HIGH";

	/**
	 * Search for a score in the cache
	 *
	 * @param key searched key
	 * @return score
	 */
	public static double getScore(String key) {
		if (loader.getCache().isEmpty()) {
			loader.loadProps(false);
		}
		try {
			return Double.parseDouble(loader.getCache().get(key));
		} catch (NumberFormatException e) {
			LOG.error("Not a valid number in properties file: " + FILE_NAME);
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}
}
