package rules.engine;

/**
 * Generic arguments, which can be used in the rule evaluator. The value
 * initializes during runtime
 */
public enum WildCards {

	/**
	 * Wildcard for the name of an entity
	 */
	ENTITY_NAME,
	/**
	 * Wildcard for the getter method of an column of an entity
	 */
	GET_METHOD,
	/**
	 * Wildcard for the setter method of an column of an entity
	 */
	SET_METHOD,
	/**
	 * Wildcard for the class of the analyzed method
	 */
	ORIGIN_CLASS,
	/**
	 * Wildcard for the analyzed method
	 */
	ORIGIN_USECASE;

	/** Prefix and Suffix of a wildcard */
	public static final String IDENTIFIER = "?";
	/** Value of the wildcard */
	private String value;

	/**
	 * @return id
	 */
	public String getId() {
		return IDENTIFIER + toString().toLowerCase() + IDENTIFIER;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public WildCards setValue(String value) {
		this.value = value;
		return this;
	}
}
