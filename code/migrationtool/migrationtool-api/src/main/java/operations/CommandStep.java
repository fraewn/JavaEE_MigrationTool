package operations;

import cmd.CommandLineParser;
import operations.dto.GenericDTO;

/**
 * Interface to define a service of the migrationtool
 */
public interface CommandStep {
	/**
	 * Set the command line arguments for the service
	 *
	 * @param args
	 */
	default void setCommandLineArguments(String[] args) {
		CommandLineParser.parse(args, this);
	}

	/**
	 * Received object from previous phase
	 *
	 * @param args
	 */
	void setDTO(GenericDTO<?> dto);

	/**
	 * Build object for next phase
	 *
	 * @param args
	 */
	GenericDTO<?> buildDTO();

	/**
	 * Execute the service
	 */
	GenericDTO<?> execute(GenericDTO<?> dto);
}
