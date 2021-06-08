package graph.clustering;

import java.util.HashMap;
import java.util.Map;

public class SolverConfiguration {

	public static final String ALGORITHMN = "ALGORITHMN";

	public static final String NUMBER_CLUSTERS = "NUMBER_CLUSTERS";

	public static final String LEUNG_PARAM_M = "LEUNG_PARAM_M";
	public static final String LEUNG_PARAM_DELTA = "LEUNG_PARAM_DELTA";

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
