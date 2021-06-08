package rules;

public enum WildCards {

	ENTITY_NAME, GET_METHOD, SET_METHOD, ORIGIN_CLASS, ORIGIN_USECASE;

	public static final String IDENTIFIER = "?";

	private String value;

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
