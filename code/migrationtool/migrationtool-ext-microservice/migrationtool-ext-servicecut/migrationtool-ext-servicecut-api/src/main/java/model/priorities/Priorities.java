package model.priorities;

import utils.DefinitionDomain;

public enum Priorities {

	/**
	 * Ignore the criteria in the resolving process
	 */
	IGNORE(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_IGNORE)),
	/**
	 * Criteria has a very low priority; value gets smaller
	 */
	LOW(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_LOW)),
	/**
	 * Neutral priority
	 */
	NORMAL(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_NORMAL)),
	/**
	 * Criteria is slightly important
	 */
	RELEVANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_RELEVANT)),
	/**
	 * Criteria is important
	 */
	IMPORTANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_IMPORTANT)),
	/**
	 * Criteria is very important
	 */
	VERY_IMPORTANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_VERY_IMPORTANT)),
	/**
	 * Criteria is critical
	 */
	CRITICAL(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_CRITICAL));

	/** Multiplier */
	private double value;

	Priorities(double value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return this.value;
	}
}
