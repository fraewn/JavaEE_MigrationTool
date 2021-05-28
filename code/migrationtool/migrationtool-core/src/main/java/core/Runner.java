package core;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import cmd.CommandLineParser;
import cmd.CommandLineSplitter;
import cmd.CommandLineValidator;
import command.AbstractCommand;
import command.ExtensionCommand;
import command.extension.DefaultCommand;
import exceptions.MigrationToolArgumentException;
import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import operations.CommandExtension;
import utils.PluginManager;

/**
 * Entry Class; Read the command argument and select the corresponding operation
 */
public class Runner {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Runner.class);

	/** value for defining global parent gui; only internal */
	@Option(name = "-localGUI", hidden = true, usage = "value for defining global parent gui")
	private boolean localGUI;

	/** value for defining the executed method */
	@Option(name = "-command", usage = "value for defining the executed method")
	private String command;

	/** list the options */
	@Option(name = "-help", usage = "get a list of all possible arguments")
	private boolean help;

	/** reference to the extension */
	private CommandExtension commandExtension;

	public void run(String[] args) {
		boolean error = false;
		try {
			execute(args);
		} catch (MigrationToolArgumentException e) {
			// user defined wrong arguments
			LOG.error(e.getMessage());
			LOG.error("Shutdown... Possible Arguments:");
			CommandLineValidator.listAllPossibleArguments(e.getObject());
			error = true;
		} catch (MigrationToolInitException e) {
			// user defined wrong parameters in properties
			LOG.error(e.getMessage());
			error = true;
		} catch (MigrationToolRuntimeException e) {
			// something went wrong
			LOG.error(e.getMessage(), e);
			error = true;
		} finally {
			if (error) {
				LOG.error("STOP EXECUTION WITH ERROR");
			} else {
				LOG.info("STOP EXECUTION");
			}
		}
	}

	private void execute(String[] args)
			throws MigrationToolArgumentException, MigrationToolInitException, MigrationToolRuntimeException {
		CmdLineParser parser = CommandLineParser.parse(args, this);
		if (this.help) {
			LOG.info("List all arguments");
			HelpFunction.listAllPossibleArguments();
		} else {
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
}
