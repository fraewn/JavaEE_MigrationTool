package data;

public class GenericDTO<T> {

	private T object;

	public GenericDTO() {

	}

	public GenericDTO(T object) {
		this.object = object;
	}

	/**
	 * @return the object
	 */
	public T getObject() {
		return this.object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}
}
