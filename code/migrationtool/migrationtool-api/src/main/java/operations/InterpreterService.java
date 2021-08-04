package operations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interpreter Service of the migration tool. Post processing of the saved meta
 * model
 *
 * @param <I> Input Object
 * @param <O> Output Object
 */
public abstract class InterpreterService<I, O> implements ProcessingStep<I, O> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	@Override
	public O process(I input) {
		LOG.info("...start interpreting...");
		O res = run(input);
		LOG.info("...finish interpreting...");
		return res;
	}

	/**
	 * Execute task on defined repository
	 */
	public abstract O run(I input);
}
