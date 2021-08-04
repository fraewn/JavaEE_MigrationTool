package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmd.CommandLineValidator;

public class HelpFunction extends CommandLineValidator {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * List all options of all services and commands
	 */
	public static void listAllPossibleArguments() {
		LOG.info("+++Arguments of the runner class+++");
		listAllPossibleArguments(new Runner());
		CommandLineValidator.listAllPossibleArguments();
	}
}
