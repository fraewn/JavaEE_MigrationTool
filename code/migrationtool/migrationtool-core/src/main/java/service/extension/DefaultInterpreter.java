package service.extension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import operations.InterpreterService;

/**
 * Dummy class for the service interpeter
 */
public class DefaultInterpreter extends InterpreterService<Object, Object> {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	@Override
	public Object run(Object input) {
		LOG.info("Run DefaultInterpreter, implement your own variant");
		return null;
	}
}
