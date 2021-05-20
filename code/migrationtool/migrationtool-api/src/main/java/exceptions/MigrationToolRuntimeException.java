package exceptions;

/**
 * Exception, thrown when there is a runtime error
 */
public class MigrationToolRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1391207132171603809L;

	public MigrationToolRuntimeException(String s) {
		super(s);
	}

	public MigrationToolRuntimeException(String message, Exception e) {
		super(message, e);
	}
}
