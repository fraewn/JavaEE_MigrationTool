package exceptions;

/**
 * Exception, thrown when the configuration is wrong
 */
public class MigrationToolInitException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2194324104424910031L;

	public MigrationToolInitException(String s) {
		super(s);
	}
}
