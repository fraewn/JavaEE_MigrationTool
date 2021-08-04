package servicesnipper.model;

public class Model {

	private String jsonStringBefore;

	private String jsonStringAfter;

	public Model() {

	}

	public Model(String jsonStringBefore, String jsonStringAfter) {
		this.jsonStringBefore = jsonStringBefore;
		this.jsonStringAfter = jsonStringAfter;
	}

	/**
	 * @return the jsonStringBefore
	 */
	public String getJsonStringBefore() {
		return this.jsonStringBefore;
	}

	/**
	 * @param jsonStringBefore the jsonStringBefore to set
	 */
	public void setJsonStringBefore(String jsonStringBefore) {
		this.jsonStringBefore = jsonStringBefore;
	}

	/**
	 * @return the jsonStringAfter
	 */
	public String getJsonStringAfter() {
		return this.jsonStringAfter;
	}

	/**
	 * @param jsonStringAfter the jsonStringAfter to set
	 */
	public void setJsonStringAfter(String jsonStringAfter) {
		this.jsonStringAfter = jsonStringAfter;
	}
}
