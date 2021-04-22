package operations;

import org.apache.log4j.Logger;

import operations.dto.GenericDTO;

/**
 * Interpreter Service of the migration tool. Post processing of the saved meta
 * model
 */
public abstract class InterpreterService implements CommandStep {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(InterpreterService.class);

	@Override
	public GenericDTO<?> execute(GenericDTO<?> dto) {
		setDTO(dto);
		LOG.info("...start interpreting...");
		run();
		LOG.info("...finish interpreting...");
		return buildDTO();
	}

	/**
	 * Execute task on defined repository
	 */
	public abstract void run();
}
