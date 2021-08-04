package service.extension;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operations.ModelService;
import operations.dto.AstDTO;

/**
 * Dummy class for the service model
 */
public class DefaultModel extends ModelService<List<AstDTO>, Object> {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	@Override
	public Object save(List<AstDTO> input) {
		LOG.info("Run DefaultModel, implement your own variant");
		return null;
	}
}
