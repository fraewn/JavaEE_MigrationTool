package operations;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import operations.dto.GenericDTO;

/**
 * Load Service of the migration tool. Find and load a external java project
 */
public abstract class LoaderService implements CommandStep {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(LoaderService.class);

	@Option(name = "-path", required = true, usage = "value for defining the location of the class files")
	protected String path;

	@Override
	public GenericDTO<?> execute(GenericDTO<?> dto) {
		setDTO(dto);
		LOG.info("...start parsing...");
		loadProject();
		LOG.info("...finish parsing...");
		return buildDTO();
	}

	/**
	 * Load all classes of a defined path
	 */
	public abstract void loadProject();
}
