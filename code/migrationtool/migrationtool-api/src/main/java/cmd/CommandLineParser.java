package cmd;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import exceptions.MigrationToolArgumentException;

/**
 * Utility class to extract the command line arguments
 */
public class CommandLineParser {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Parse the arguments of the program
	 *
	 * @param args list of arguments
	 * @param o    Class with arguments
	 * @return used CmdLineParser
	 */
	public static CmdLineParser parse(String[] args, Object o) {
		String vars = Arrays.toString(args).replaceAll("\\[|\\]", "").replaceAll(", ", System.lineSeparator() + "\t");
		LOG.debug("Remaining arguments: [{}\t{}{}]", System.lineSeparator(), vars, System.lineSeparator());
		CmdLineParser parser = new CmdLineParser(o);
		try {
			LOG.debug("parse Arguments of {}", o.getClass().getSimpleName());
			parser.parseArgument(CommandLineSplitter.definedArgs(args, parser));
		} catch (CmdLineException e) {
			throw new MigrationToolArgumentException(e.getMessage(), o);
		}
		return parser;
	}

	/**
	 * Parse the arguments of the program
	 *
	 * @param args list of arguments
	 * @param o    Class with arguments
	 * @return used CmdLineParser
	 */
	public static CmdLineParser parseIgnoreErrors(String[] args, Object o) {
		CmdLineParser parser = null;
		try {
			parser = parse(args, o);
		} catch (MigrationToolArgumentException e) {
			// handle exception
		}
		return parser;
	}
}
