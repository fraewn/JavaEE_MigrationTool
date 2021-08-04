package operations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model Service of the migration tool. Save the meta definitions of a external
 * java project
 *
 * @param <I> Input Object
 * @param <O> Output Object
 */
public abstract class ModelService<I, O> implements ProcessingStep<I, O> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	@Override
	public O process(I input) {
		LOG.info("...start saving...");
		O res = save(input);
		LOG.info("...finish saving...");
		return res;
	}

	/**
	 * Save to a defined path
	 */
	public abstract O save(I input);
}
