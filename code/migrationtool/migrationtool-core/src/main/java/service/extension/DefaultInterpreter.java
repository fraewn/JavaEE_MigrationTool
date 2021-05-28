package service.extension;

import org.apache.log4j.Logger;

import operations.InterpreterService;

/**
 * Dummy class for the service interpeter
 */
public class DefaultInterpreter extends InterpreterService<Object, Object> {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(DefaultInterpreter.class);

	@Override
	public Object run(Object input) {
		LOG.info("Run DefaultInterpreter, implement your own variant");
		return null;
	}
}
