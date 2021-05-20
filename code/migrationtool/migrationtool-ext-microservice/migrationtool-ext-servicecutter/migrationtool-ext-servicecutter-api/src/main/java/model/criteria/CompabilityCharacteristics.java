package model.criteria;

/**
 * Supported Characterisitcs for each {@link CouplingGroup#COMPATIBILITY}
 */
public enum CompabilityCharacteristics {

	// @formatter:off
	CC_4_LOW(0, "Rarely"),
	CC_4_NORMAL(4, "Normal", true),
	CC_4_HIGH(10, "Often"),

	CC_6_LOW(0, "Weak"),
	CC_6_NORMAL(4, "Eventually"),
	CC_6_HIGH(10, "High", true),

	CC_7_LOW(0, "Low"),
	CC_7_NORMAL(4, "Normal", true),
	CC_7_HIGH(10, "Critical"),

	CC_8_LOW(0, "Rarely"),
	CC_8_NORMAL(5, "Normal", true),
	CC_8_HIGH(10, "Often"),

	CC_10_LOW(0, "Tiny"),
	CC_10_NORMAL(3, "Normal", true),
	CC_10_HIGH(10, "Huge"),

	CC_13_LOW(0, "Public"),
	CC_13_NORMAL(3, "Internal", true),
	CC_13_HIGH(10, "Critical");
	// @formatter:on

	private int weight;

	private String name;

	private boolean defaultSelection;

	private CompabilityCharacteristics(int weight, String name) {
		this.weight = weight;
		this.name = name;
	}

	private CompabilityCharacteristics(int weight, String name, boolean defaultSelection) {
		this.weight = weight;
		this.name = name;
		this.defaultSelection = defaultSelection;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
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
