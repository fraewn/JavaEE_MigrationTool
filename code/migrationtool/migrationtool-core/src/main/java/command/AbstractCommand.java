package command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import cmd.CommandLineParser;
import cmd.CommandLineSplitter;
import exceptions.MigrationToolInitException;
import operations.CommandExtension;
import operations.CommandStep;
import operations.dto.GenericDTO;
import utils.PluginManager;

/**
 * Abstract class of all possible command. Defines a basic structure of the
 * processing
 */
public abstract class AbstractCommand extends CommandExtension {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(AbstractCommand.class);

	/** List of the operations */
	private List<CommandStep> steps;

	/** Map of the defined operations */
	private Map<String, Class<? extends CommandStep>> definedSteps;

	/** Temp var, save unknown arguments */
	protected String[] arguments;

	public AbstractCommand() {
		this.steps = new ArrayList<>();
		this.definedSteps = new LinkedHashMap<>();
	}

	/**
	 * Define the name of the command
	 *
	 * @return command name
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

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
				throw new MigrationToolInitException(
						"No Implementation found for " + step.getValue() + " <-> " + step.getKey());
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
		GenericDTO<?> dto = null;
		for (CommandStep commandStep : this.steps) {
			// push unknown arguments to service definition
			commandStep.setCommandLineArguments(this.arguments);
			dto = commandStep.execute(dto);
		}
		afterExecution();
		LOG.info("Done...");
	}

	/**
	 * Process before Initialization
	 */
	@Override
	public void beforeInitialization() {
		// Overwrite
	}

	/**
	 * Process after Initialization
	 */
	@Override
	public void afterInitialization() {
		// Overwrite
	}

	/**
	 * Process after Execution
	 */
	@Override
	public void afterExecution() {
		// Overwrite
	}

	/**
	 * Set all defined arguments to the command. Fill list of unknown arguments
	 *
	 * @param args command arguments
	 */
	protected void setCommandLineArguments(String[] args) {
		CmdLineParser parser = CommandLineParser.parse(args, getCmdObject());
		this.arguments = CommandLineSplitter.undefinedArgs(args, parser);
	}

	/**
	 * Get the object with cmd parameters
	 */
	protected Object getCmdObject() {
		return this;
	}
}
