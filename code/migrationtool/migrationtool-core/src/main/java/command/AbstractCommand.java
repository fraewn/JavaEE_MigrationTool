package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import api.CommandStep;
import utils.CommanLineSplitter;
import utils.MigrationToolInitException;
import utils.PluginManager;

public abstract class AbstractCommand {

	private static final Logger LOG = Logger.getLogger(AbstractCommand.class);

	private List<CommandStep> steps;

	private String[] arguments;

	public AbstractCommand() {
		this.steps = new ArrayList<>();
	}

	public abstract String getName();

	protected abstract SortedMap<String, Class<? extends CommandStep>> defineSteps();

	private void loadSteps() {
		SortedMap<String, Class<? extends CommandStep>> list = defineSteps();
		for (Map.Entry<String, Class<? extends CommandStep>> step : list.entrySet()) {
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
			commandStep.setCommandLineArguments(this.arguments);
			commandStep.execute();
		}
		afterExecution();
		LOG.info("Done...");
	}

	protected void beforeInitialization() {
		// Overwrite
	}

	protected void afterInitialization() {
		// Overwrite
	}

	protected void afterExecution() {
		// Overwrite
	}

	protected void setCommandLineArguments(String[] args) {
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
		this.arguments = CommanLineSplitter.undefinedArgs(args, parser);
	}
}
