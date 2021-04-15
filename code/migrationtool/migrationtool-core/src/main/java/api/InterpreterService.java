package api;

/**
 * Interpreter Service of the migration tool. Post processing of the saved meta
 * model
 */
public interface InterpreterService extends CommandStep {

	@Override
	default void execute() {

	}
}
