package model.criteria;

import resolver.Scores;

/**
 * Supported Characterisitcs for each {@link CouplingGroup#COMPATIBILITY}
 */
public enum CompabilityCharacteristics {

	// @formatter:off
	CC_4_LOW(Scores.getScore(Scores.CC_4_LOW), "Rarely"),
	CC_4_NORMAL(Scores.getScore(Scores.CC_4_NORMAL), "Normal", true),
	CC_4_HIGH(Scores.getScore(Scores.CC_4_HIGH), "Often"),

	CC_6_LOW(Scores.getScore(Scores.CC_6_LOW), "Weak"),
	CC_6_NORMAL(Scores.getScore(Scores.CC_6_NORMAL), "Eventually"),
	CC_6_HIGH(Scores.getScore(Scores.CC_6_HIGH), "High", true),

	CC_7_LOW(Scores.getScore(Scores.CC_7_LOW), "Low"),
	CC_7_NORMAL(Scores.getScore(Scores.CC_7_NORMAL), "Normal", true),
	CC_7_HIGH(Scores.getScore(Scores.CC_7_HIGH), "Critical"),

	CC_8_LOW(Scores.getScore(Scores.CC_8_LOW), "Rarely"),
	CC_8_NORMAL(Scores.getScore(Scores.CC_8_NORMAL), "Normal", true),
	CC_8_HIGH(Scores.getScore(Scores.CC_8_HIGH), "Often"),

	CC_10_LOW(Scores.getScore(Scores.CC_10_LOW), "Tiny"),
	CC_10_NORMAL(Scores.getScore(Scores.CC_10_NORMAL), "Normal", true),
	CC_10_HIGH(Scores.getScore(Scores.CC_10_HIGH), "Huge"),

	CC_13_LOW(Scores.getScore(Scores.CC_13_LOW), "Public"),
	CC_13_NORMAL(Scores.getScore(Scores.CC_13_NORMAL), "Internal", true),
	CC_13_HIGH(Scores.getScore(Scores.CC_13_HIGH), "Critical");
	// @formatter:on

	private double weight;

	private String name;

	private boolean defaultSelection;

	private CompabilityCharacteristics(double weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	private CompabilityCharacteristics(double weight, String name, boolean defaultSelection) {
		this.weight = weight;
		this.name = name;
		this.defaultSelection = defaultSelection;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the defaultSelection
	 */
	public boolean isDefaultSelection() {
		return this.defaultSelection;
	}
}
