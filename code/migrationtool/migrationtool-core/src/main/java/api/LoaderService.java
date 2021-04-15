package api;

/**
 * Load Service of the migration tool. Find and load a external java project
 */
public interface LoaderService extends CommandStep {

	@Override
	default void execute() {
		findClasses();
	}

	/**
	 * Load all classes of a defined path
	 */
	void findClasses();

	/**
	 * Define all Visitors of this load operation
	 */
	void defineVisitors();

	/**
	 * Save the result of the visitors
	 */
	void saveVisitorResult();
}
