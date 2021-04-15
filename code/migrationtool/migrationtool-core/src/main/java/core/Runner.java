package core;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import command.AbstractCommand;
import utils.CommanLineSplitter;
import utils.PluginManager;

public class Runner {

	private static final Logger LOG = Logger.getLogger(Runner.class);

	@Option(name = "-command", required = true, usage = "value for defining the executed method")
	private String command;

	private AbstractCommand runnableCommand;

	public void run(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(CommanLineSplitter.definedArgs(args, parser));
		} catch (CmdLineException e) {
			// this will report an error message.
			LOG.error(e.getMessage());
			LOG.error("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
		}
		this.runnableCommand = PluginManager.findPluginCommand(AbstractCommand.class, this.command);
		if (this.runnableCommand == null) {
			LOG.error("Command not found: " + this.command);
		} else {
			this.runnableCommand.run(CommanLineSplitter.undefinedArgs(args, parser));
		}
	}
}
