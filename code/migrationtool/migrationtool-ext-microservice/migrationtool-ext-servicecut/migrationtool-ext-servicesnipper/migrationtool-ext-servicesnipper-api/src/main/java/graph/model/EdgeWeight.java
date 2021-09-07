package graph.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

/**
 * Saves the weight of an edge grouped by coupling criteria
 */
public class EdgeWeight {

	/** data structure */
	private Map<CouplingCriteria, Double> weight;

	public EdgeWeight() {
		this.weight = new HashMap<>();
	}

	/**
	 * Sets a new value to the weight of the defined coupling criteria
	 *
	 * @param criteria coupling criteria
	 * @param value    new value
	 */
	public void addNewValue(CouplingCriteria criteria, Double value) {
		this.weight.put(criteria, value);
	}

	/**
	 * Gets the value of the weight of the defined coupling criteria
	 *
	 * @param criteria coupling criteria
	 * @return weight
	 */
	public Double getScoreOfCriteria(CouplingCriteria criteria) {
		return this.weight.get(criteria);
	}

	/**
	 * @return Gets the value of the weight
	 */
	public double getTotalScore() {
		double sum = 0;
		for (Double value : this.weight.values()) {
			sum += Optional.ofNullable(value).orElse(0d);
		}
		return sum;
	}

	/**
	 * Gets the value of the weight with the multiplicator of priorities
	 *
	 * @param priorities
	 * @return value of the weight
	 */
	public double getFinalScore(Map<CouplingCriteria, Priorities> priorities) {
		if (priorities == null) {
			return getTotalScore();
		}
		double sum = 0;
		for (Entry<CouplingCriteria, Double> entry : this.weight.entrySet()) {
			Double temp = this.weight.get(entry.getKey());
			sum += temp * priorities.get(entry.getKey()).getValue();
		}
		return sum;
	}
}
