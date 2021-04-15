package utils;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Utility class to extract the command line arguments
 */
public class CommandLineParser {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(CommandLineParser.class);

	/**
	 * Parse the arguments of the program
	 *
	 * @param args list of arguments
	 * @param o    Class with arguments
	 * @return used CmdLineParser
	 */
	public static CmdLineParser parse(String[] args, Object o) {
		CmdLineParser parser = new CmdLineParser(o);
		try {
			// parse only defined arguments.
			parser.parseArgument(CommandLineSplitter.definedArgs(args, parser));
		} catch (CmdLineException e) {
			// this will report an error message.
			LOG.error(e.getMessage());
			// print the list of available options
			parser.printUsage(System.err);
		}
		return parser;
	}
}
