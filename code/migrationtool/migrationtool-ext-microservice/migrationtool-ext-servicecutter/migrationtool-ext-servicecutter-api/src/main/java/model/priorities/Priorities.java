package model.priorities;

import utils.DefinitionDomain;

public enum Priorities {

	/**
	 *
	 */
	IGNORE(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_IGNORE)),
	/**
	 *
	 */
	LOW(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_LOW)),
	/**
	 *
	 */
	NORMAL(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_NORMAL)),
	/**
	 *
	 */
	RELEVANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_RELEVANT)),
	/**
	 *
	 */
	IMPORTANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_IMPORTANT)),
	/**
	 *
	 */
	VERY_IMPORTANT(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_VERY_IMPORTANT)),
	/**
	 *
	 */
	CRITICAL(DefinitionDomain.getScore(DefinitionDomain.PRIORITY_CRITICAL));

	private double value;

	private Priorities(double value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return this.value;
	}
}
