package core;

import org.apache.log4j.Logger;

import cmd.CommandLineParser;
import cmd.CommandLineValidator;

public class HelpFunction extends CommandLineValidator {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(CommandLineParser.class);

	/**
	 * List all options of all services and commands
	 */
	public static void listAllPossibleArguments() {
		LOG.info("+++Arguments of the runner class+++");
		listAllPossibleArguments(new Runner());
		CommandLineValidator.listAllPossibleArguments();
	}
}
