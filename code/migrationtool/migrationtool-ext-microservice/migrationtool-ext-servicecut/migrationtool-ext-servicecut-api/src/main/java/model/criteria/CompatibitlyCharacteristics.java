package model.criteria;

import static utils.DefinitionDomain.getScore;

import utils.DefinitionDomain;

/**
 * Supported Characterisitcs for each {@link CouplingGroup#COMPATIBILITY}
 */
public enum CompatibitlyCharacteristics {

	// @formatter:off
	CC_9_LOW(getScore(DefinitionDomain.CC_9_LOW), "Rarely"),
	CC_9_NORMAL(getScore(DefinitionDomain.CC_9_NORMAL), "Normal", true),
	CC_9_HIGH(getScore(DefinitionDomain.CC_9_HIGH), "Often"),

	CC_10_LOW(getScore(DefinitionDomain.CC_10_LOW), "Rarely"),
	CC_10_NORMAL(getScore(DefinitionDomain.CC_10_NORMAL), "Normal", true),
	CC_10_HIGH(getScore(DefinitionDomain.CC_10_HIGH), "Often"),

	CC_11_LOW(getScore(DefinitionDomain.CC_11_LOW), "Weak"),
	CC_11_NORMAL(getScore(DefinitionDomain.CC_11_NORMAL), "Eventually"),
	CC_11_HIGH(getScore(DefinitionDomain.CC_11_HIGH), "High", true),

	CC_12_LOW(getScore(DefinitionDomain.CC_12_LOW), "Low"),
	CC_12_NORMAL(getScore(DefinitionDomain.CC_12_NORMAL), "Normal", true),
	CC_12_HIGH(getScore(DefinitionDomain.CC_12_HIGH), "Critical"),

	CC_13_LOW(getScore(DefinitionDomain.CC_13_LOW), "Tiny"),
	CC_13_NORMAL(getScore(DefinitionDomain.CC_13_NORMAL), "Normal", true),
	CC_13_HIGH(getScore(DefinitionDomain.CC_13_HIGH), "Huge"),

	CC_14_LOW(getScore(DefinitionDomain.CC_14_LOW), "Public"),
	CC_14_NORMAL(getScore(DefinitionDomain.CC_14_NORMAL), "Internal", true),
	CC_14_HIGH(getScore(DefinitionDomain.CC_14_HIGH), "Critical");
	// @formatter:on

	/** multiplyer in the resolving process */
	private double weight;
	/** display name */
	private String name;
	/** is default of group */
	private boolean defaultSelection;

	CompatibitlyCharacteristics(double weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	CompatibitlyCharacteristics(double weight, String name, boolean defaultSelection) {
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
