package model.data;

import resolver.Scores;

public enum Priorities {

	/**
	 *
	 */
	IGNORE(Scores.getScore(Scores.PRIORITY_IGNORE)),
	/**
	 *
	 */
	LOW(Scores.getScore(Scores.PRIORITY_LOW)),
	/**
	 *
	 */
	NORMAL(Scores.getScore(Scores.PRIORITY_NORMAL)),
	/**
	 *
	 */
	RELEVANT(Scores.getScore(Scores.PRIORITY_RELEVANT)),
	/**
	 *
	 */
	IMPORTANT(Scores.getScore(Scores.PRIORITY_IMPORTANT)),
	/**
	 *
	 */
	VERY_IMPORTANT(Scores.getScore(Scores.PRIORITY_VERY_IMPORTANT)),
	/**
	 *
	 */
	CRITICAL(Scores.getScore(Scores.PRIORITY_CRITICAL));

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
