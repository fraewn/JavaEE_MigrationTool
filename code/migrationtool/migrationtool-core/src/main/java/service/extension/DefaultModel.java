package service.extension;

import org.apache.log4j.Logger;

import operations.ModelService;
import operations.dto.GenericDTO;

/**
 * Dummy class for the service model
 */
public class DefaultModel extends ModelService {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(DefaultModel.class);

	@Override
	public void setDTO(GenericDTO<?> dto) {
		// Nothing
	}

	@Override
	public GenericDTO<?> buildDTO() {
		// Nothing
		return null;
	}

	@Override
	public void save() {
		LOG.info("Run DefaultModel, implement your own variant");
	}
}
