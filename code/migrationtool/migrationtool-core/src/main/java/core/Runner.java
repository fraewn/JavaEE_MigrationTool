package core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Runner {

	private static final Logger LOG = Logger.getLogger(Runner.class);

	@Option(name = "-command", usage = "boolean value for checking the custom handler")
	private String command;

	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<>();

	public void run(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(args);
			// after parsing arguments, you should check if enough arguments are given.
			if (this.arguments.isEmpty()) {
				throw new CmdLineException(parser, "No argument is given");
			}
		} catch (CmdLineException e) {
			// this will report an error message.
			LOG.error(e.getMessage());
			LOG.error("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
		}

		// TODO load correct command

	}
}
