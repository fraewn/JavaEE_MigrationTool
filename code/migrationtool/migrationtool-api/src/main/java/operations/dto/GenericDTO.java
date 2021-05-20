package operations.dto;

/**
 * Generic DTO class, which is exchanged between the different operations
 *
 * @param <T> Passed Object
 */
public class GenericDTO<T> {
	/** passed Object */
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
