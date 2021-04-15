package api;

public interface LoaderService extends CommandStep {

	@Override
	default void execute() {
		findClasses();
	}

	void findClasses();

	void defineVisitors();

	void saveVisitorResult();
}
