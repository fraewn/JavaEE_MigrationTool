package exceptions;

/**
 * Exception, thrown when the configuration in the command line is wrong
 */
public class GraphInitException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2194324104424910031L;

	public GraphInitException(String s) {
		super(s);
	}
}
