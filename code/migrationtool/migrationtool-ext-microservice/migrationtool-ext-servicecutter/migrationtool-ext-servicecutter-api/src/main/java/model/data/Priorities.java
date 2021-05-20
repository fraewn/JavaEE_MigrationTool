package model.data;

public enum Priorities {

	/**
	 *
	 */
	IGNORE(0d),
	/**
	 *
	 */
	LOW(0.5),
	/**
	 *
	 */
	NORMAL(1d),
	/**
	 *
	 */
	RELEVANT(3d),
	/**
	 *
	 */
	IMPORTANT(5d),
	/**
	 *
	 */
	VERY_IMPORTANT(8d),
	/**
	 *
	 */
	CRITICAL(13d);

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
