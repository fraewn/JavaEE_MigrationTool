package exceptions;

/**
 * Exception, thrown when there is a runtime error
 */
public class GraphRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1391207132171603809L;

	public GraphRuntimeException(String s) {
		super(s);
	}

	public GraphRuntimeException(String message, Exception e) {
		super(message, e);
	}
}
