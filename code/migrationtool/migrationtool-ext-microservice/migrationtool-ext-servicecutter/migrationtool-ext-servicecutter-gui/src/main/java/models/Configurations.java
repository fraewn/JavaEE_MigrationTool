package models;

import java.util.HashMap;
import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public class Configurations {

	private ObjectProperty<ClusterAlgorithms> selectedAlgorithmn;

	private Map<String, StringProperty> algorithmnSettings;

	private Map<CouplingCriteria, ObjectProperty<Priorities>> bindValues;

	public Configurations() {
		this.bindValues = new HashMap<>();
		this.algorithmnSettings = new HashMap<>();
	}

	/**
	 * @return the bindValues
	 */
	public Map<CouplingCriteria, ObjectProperty<Priorities>> getBindValues() {
		return this.bindValues;
	}

	/**
	 * @return the algorithmnSettings
	 */
	public Map<String, StringProperty> getAlgorithmnSettings() {
		return algorithmnSettings;
	}

	/**
	 * @return the selectedAlgorithmn
	 */
	public ObjectProperty<ClusterAlgorithms> getSelectedAlgorithmn() {
		return selectedAlgorithmn;
	}

	/**
	 * @param selectedAlgorithmn the selectedAlgorithmn to set
	 */
	public void setSelectedAlgorithmn(ObjectProperty<ClusterAlgorithms> selectedAlgorithmn) {
		this.selectedAlgorithmn = selectedAlgorithmn;
	}
}
