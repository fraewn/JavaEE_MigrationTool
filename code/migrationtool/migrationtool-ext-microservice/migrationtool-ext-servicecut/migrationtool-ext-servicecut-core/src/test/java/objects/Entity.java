package objects;

@DefEntity
public class Entity {

	private String id;

	private int value;

	private boolean required;

	private SecondEntity second;

	public static String notIncluded = "Test";

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the second
	 */
	public SecondEntity getSecond() {
		return this.second;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(SecondEntity second) {
		this.second = second;
	}
}
