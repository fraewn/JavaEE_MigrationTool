package core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import command.AbstractCommand;

public class Runner {

	private static final Logger LOG = Logger.getLogger(Runner.class);

	@Option(name = "-command", required = true, usage = "value for defining the executed method")
	private String command;

	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<>();

	private AbstractCommand runnableCommand;

	public void run(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			// this will report an error message.
			LOG.error(e.getMessage());
			LOG.error("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
		}
		this.runnableCommand = CommandLoader.get(this.command);
		if (this.runnableCommand == null) {
			LOG.error("Command not found: " + this.command);
		} else {
			this.runnableCommand.run(this.arguments.toArray(new String[0]));
		}
	}
}
