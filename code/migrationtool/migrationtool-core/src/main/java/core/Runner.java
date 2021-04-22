package core;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import cmd.CommandLineParser;
import cmd.CommandLineSplitter;
import command.AbstractCommand;
import command.DefaultCommand;
import command.ExtensionCommand;
import operations.CommandExtension;
import utils.PluginManager;

/**
 * Entry Class; Read the command argument and select the corresponding operation
 */
public class Runner {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Runner.class);

	/** value for defining the executed method */
	@Option(name = "-command", usage = "value for defining the executed method")
	private String command;

	/** reference to the extension */
	private CommandExtension commandExtension;

	public void run(String[] args) {
		CmdLineParser parser = CommandLineParser.parse(args, this);
		this.commandExtension = PluginManager.findPluginCommand(CommandExtension.class, this.command);
		AbstractCommand runnableCommand = null;
		if (this.commandExtension == null) {
			LOG.info("No Command specified, uses default");
			runnableCommand = new DefaultCommand();
		} else {
			LOG.info("Command specified, uses " + this.commandExtension.getClass().getSimpleName());
			runnableCommand = new ExtensionCommand(this.commandExtension);
		}
		// run selected command; push unknown arguments to command definition
		runnableCommand.run(CommandLineSplitter.undefinedArgs(args, parser));
	}
}
