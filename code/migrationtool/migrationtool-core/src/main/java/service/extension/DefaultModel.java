package service.extension;

import java.util.List;

import org.apache.log4j.Logger;

import operations.ModelService;
import operations.dto.ClassDTO;

/**
 * Dummy class for the service model
 */
public class DefaultModel extends ModelService<List<ClassDTO>, Object> {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(DefaultModel.class);

	@Override
	public Object save(List<ClassDTO> input) {
		LOG.info("Run DefaultModel, implement your own variant");
		return null;
	}
}
