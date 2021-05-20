package exceptions;

/**
 * Exception, thrown when the configuration in the command line is wrong
 */
public class MigrationToolArgumentException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2194324104424910031L;

	private Object object;

	public MigrationToolArgumentException(String s, Object object) {
		super(s);
		this.object = object;
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return this.object;
	}
}
