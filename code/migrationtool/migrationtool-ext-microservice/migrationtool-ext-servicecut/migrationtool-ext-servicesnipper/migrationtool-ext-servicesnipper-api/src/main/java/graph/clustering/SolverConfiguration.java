package graph.clustering;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for all possible cluster algorithm settings
 */
public class SolverConfiguration {

	/** Name of the used algorithmn */
	public static final String ALGORITHMN = "ALGORITHMN";

	// Settings GIRVAN NEWMAN
	/** Count of result clusters */
	public static final String NUMBER_CLUSTERS = "NUMBER_CLUSTERS";

	// Settings LEUNG
	/** Count of result clusters */
	public static final String LEUNG_PARAM_M = "LEUNG_PARAM_M";
	/** Count of result clusters */
	public static final String LEUNG_PARAM_DELTA = "LEUNG_PARAM_DELTA";

	// Settings CHINESE_WHISPERS
	/** Count of result clusters */
	public static final String CHINESE_WHISPERS_NODE_WEIGHT = "CHINESE_WHISPERS_NODE_WEIGHT";

	// Settings MARKOV
	/** Count of result clusters */
	public static final String MARKOV_NUMBER_OF_EXPANSIONS = "MARKOV_NUMBER_OF_EXPANSIONS";
	/** Count of result clusters */
	public static final String MARKOV_POWER_COEFFICENT = "MARKOV_POWER_COEFFICENT";

	/** list of current configurations */
	private Map<String, String> config;

	public SolverConfiguration() {
		this.config = new HashMap<>();
	}

	/**
	 * @return the config
	 */
	public Map<String, String> getConfig() {
		return this.config;
	}
}
