package operations;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

/**
 * Load Service of the migration tool. Find and load a external java project
 * 
 * @param <I>
 * @param <O>
 */
public abstract class LoaderService<I, O> implements ProcessingStep<I, O> {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(LoaderService.class);

	@Option(name = "-path", required = true, usage = "value for defining the location of the class files")
	protected String path;

	@Override
	public O process(I input) {
		LOG.info("...start parsing...");
		O res = loadProject(input);
		LOG.info("...finish parsing...");
		return res;
	}

	/**
	 * Load all classes of a defined path
	 */
	public abstract O loadProject(I input);
}
