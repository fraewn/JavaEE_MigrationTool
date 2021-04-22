package operations;

import org.apache.log4j.Logger;

import operations.dto.GenericDTO;

/**
 * Model Service of the migration tool. Save the meta definitions of a external
 * java project
 */
public abstract class ModelService implements CommandStep {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(ModelService.class);

	@Override
	public GenericDTO<?> execute(GenericDTO<?> dto) {
		setDTO(dto);
		LOG.info("...start saving...");
		save();
		LOG.info("...finish saving...");
		return buildDTO();
	}

	/**
	 * Save to a defined path
	 */
	public abstract void save();
}
