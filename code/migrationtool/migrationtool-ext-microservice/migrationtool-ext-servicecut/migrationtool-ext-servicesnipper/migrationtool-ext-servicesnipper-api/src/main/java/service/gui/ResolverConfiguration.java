package service.gui;

import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

/**
 * Wrapper class for all necessary settings to execute the service cut resolver
 */
public class ResolverConfiguration {
	/** selected algorithm */
	private ClusterAlgorithms selectedAlgorithmn;
	/** algorithm settings */
	private Map<String, String> settings;
	/** priorities of coupling criteria */
	private Map<CouplingCriteria, Priorities> priorities;

	/**
	 * @return the selectedAlgorithmn
	 */
	public ClusterAlgorithms getSelectedAlgorithmn() {
		return this.selectedAlgorithmn;
	}

	/**
	 * @param selectedAlgorithmn the selectedAlgorithmn to set
	 */
	public void setSelectedAlgorithmn(ClusterAlgorithms selectedAlgorithmn) {
		this.selectedAlgorithmn = selectedAlgorithmn;
	}

	/**
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return this.settings;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	/**
	 * @return the priorities
	 */
	public Map<CouplingCriteria, Priorities> getPriorities() {
		return this.priorities;
	}

	/**
	 * @param priorities the priorities to set
	 */
	public void setPriorities(Map<CouplingCriteria, Priorities> priorities) {
		this.priorities = priorities;
	}
}
