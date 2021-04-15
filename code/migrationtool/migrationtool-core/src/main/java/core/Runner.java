package core;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import command.AbstractCommand;
import utils.CommandLineParser;
import utils.CommandLineSplitter;
import utils.PluginManager;

/**
 * Entry Class; Read the command argument and select the corresponding operation
 */
public class Runner {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Runner.class);

	/** value for defining the executed method */
	@Option(name = "-command", required = true, usage = "value for defining the executed method")
	private String command;

	/** reference to the selected operation */
	private AbstractCommand runnableCommand;

	public void run(String[] args) {
		CmdLineParser parser = CommandLineParser.parse(args, this);
		this.runnableCommand = PluginManager.findPluginCommand(AbstractCommand.class, this.command);
		if (this.runnableCommand == null) {
			LOG.error("Command not found: " + this.command);
		} else {
			// run selected command; push unknown arguments to command definition
			this.runnableCommand.run(CommandLineSplitter.undefinedArgs(args, parser));
		}
	}
}
