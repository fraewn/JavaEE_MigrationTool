package command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import api.CommandStep;
import exceptions.MigrationToolInitException;
import utils.CommandLineParser;
import utils.CommandLineSplitter;
import utils.PluginManager;

/**
 * Abstract class of all possible command. Defines a basic structure of the
 * processing
 */
public abstract class AbstractCommand {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(AbstractCommand.class);

	/** List of the operations */
	private List<CommandStep> steps;

	/** Map of the defined operations */
	private Map<String, Class<? extends CommandStep>> definedSteps;

	/** Temp var, save unknown arguments */
	private String[] arguments;

	public AbstractCommand() {
		this.steps = new ArrayList<>();
		this.definedSteps = new LinkedHashMap<>();
	}

	/**
	 * Define the name of the command
	 *
	 * @return command name
	 */
	public abstract String getName();

	/**
	 * Define the order of possible services for this command
	 *
	 * @param definedSteps sorted list
	 */
	protected abstract void defineSteps(Map<String, Class<? extends CommandStep>> definedSteps);

	/*
	 * Load with the help of the PluginManager all defined services
	 */
	private void loadSteps() {
		defineSteps(this.definedSteps);
		for (Map.Entry<String, Class<? extends CommandStep>> step : this.definedSteps.entrySet()) {
			if (step == null) {
				throw new MigrationToolInitException("No Implementation declared");
			}
			int foundImpl = this.steps.size();
			CommandStep tmp = PluginManager.findPluginService(step.getValue(), step.getKey());
			if (tmp != null) {
				this.steps.add(tmp);
			}
			if (foundImpl == this.steps.size()) {
				throw new MigrationToolInitException("No Implementation found");
			}
		}
	}

	/**
	 * Execute the command
	 *
	 * @param args command arguments
	 */
	public void run(String[] args) {
		LOG.info("Execute command " + getName());
		LOG.info("Initialize...");
		beforeInitialization();
		setCommandLineArguments(args);
		loadSteps();
		afterInitialization();
		LOG.info("Defined ExecutionOrder");
		for (CommandStep commandStep : this.steps) {
			LOG.info(">" + commandStep.getClass().getSimpleName());
		}
		LOG.info("Execute...");
		for (CommandStep commandStep : this.steps) {
			// set arguments of service
			commandStep.setCommandLineArguments(this.arguments);
			commandStep.execute();
		}
		afterExecution();
		LOG.info("Done...");
	}

	/**
	 * Process before Initialization
	 */
	protected void beforeInitialization() {
		// Overwrite
	}

	/**
	 * Process after Initialization
	 */
	protected void afterInitialization() {
		// Overwrite
	}

	/**
	 * Process after Execution
	 */
	protected void afterExecution() {
		// Overwrite
	}

	/**
	 * Set all defined arguments to the command. Fill list of unknown arguments
	 *
	 * @param args command arguments
	 */
	protected void setCommandLineArguments(String[] args) {
		CmdLineParser parser = CommandLineParser.parse(args, this);
		this.arguments = CommandLineSplitter.undefinedArgs(args, parser);
	}
}
