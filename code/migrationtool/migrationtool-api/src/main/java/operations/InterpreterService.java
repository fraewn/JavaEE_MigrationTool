package operations;

import org.apache.log4j.Logger;

/**
 * Interpreter Service of the migration tool. Post processing of the saved meta
 * model
 */
public abstract class InterpreterService<I, O> implements ProcessingStep<I, O> {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(InterpreterService.class);

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
