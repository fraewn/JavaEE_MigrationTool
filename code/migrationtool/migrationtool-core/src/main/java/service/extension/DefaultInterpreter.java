package service.extension;

import org.apache.log4j.Logger;

import operations.InterpreterService;
import operations.dto.GenericDTO;

public class DefaultInterpreter extends InterpreterService {

	private static final Logger LOG = Logger.getLogger(DefaultInterpreter.class);

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
	public void run() {
		LOG.info("Run DefaultInterpreter, implement your own variant");
	}
}
