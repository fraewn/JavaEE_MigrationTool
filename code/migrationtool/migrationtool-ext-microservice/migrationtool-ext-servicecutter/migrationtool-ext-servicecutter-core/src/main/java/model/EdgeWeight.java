package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public class EdgeWeight {

	private Map<CouplingCriteria, Double> weight;

	public EdgeWeight() {
		this.weight = new HashMap<>();
	}

	public void addNewValue(CouplingCriteria criteria, Double value) {
		this.weight.put(criteria, value);
	}

	public double getTotalScore() {
		double sum = 0;
		for (Entry<CouplingCriteria, Double> entry : this.weight.entrySet()) {
			sum += this.weight.get(entry.getKey());
		}
		return sum;
	}

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

	/**
	 * @return the weight
	 */
	public Map<CouplingCriteria, Double> getWeight() {
		return this.weight;
	}
}
