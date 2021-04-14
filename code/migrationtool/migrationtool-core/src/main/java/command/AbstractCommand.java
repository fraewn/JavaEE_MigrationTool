package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import api.CommandStep;
import utils.MigrationToolInitException;

public abstract class AbstractCommand {

	private static final Logger LOG = Logger.getLogger(AbstractCommand.class);

	private static final String SERVICE_EXTENSION_PACKAGE = "service.extension";

	private List<CommandStep> steps;

	public AbstractCommand() {
		this.steps = new ArrayList<>();
	}

	public abstract String getName();

	protected abstract SortedMap<String, Class<? extends CommandStep>> defineSteps();

	protected abstract void setCommandLineArguments(String[] args);

	private void loadSteps() {
		SortedMap<String, Class<? extends CommandStep>> list = defineSteps();
		for (Map.Entry<String, Class<? extends CommandStep>> step : list.entrySet()) {
			if (step == null) {
				throw new MigrationToolInitException("No Implementation declared");
			}
			int foundImpl = this.steps.size();
			ServiceLoader.load(step.getValue()).forEach(plugin -> {
				System.out.println(plugin);

				String path = SERVICE_EXTENSION_PACKAGE + "." + step.getKey();
				System.out.println(path);
				System.out.println(plugin.getClass().getName());
				if (path.equals(plugin.getClass().getName())) {
					this.steps.add(plugin);
				}
			});
			if (foundImpl == this.steps.size()) {
				throw new MigrationToolInitException("No Implementation found");
			}
		}
	}

	public void run(String[] args) {
		LOG.info("Execute command " + getName());
		LOG.info("Initialize...");
		beforeInitialization();
		loadSteps();
		setCommandLineArguments(args);
		afterInitialization();
		LOG.info("Defined ExecutionOrder");
		for (CommandStep commandStep : this.steps) {
			LOG.info(">" + commandStep.getClass().getSimpleName());
		}
		LOG.info("Execute...");
		for (CommandStep commandStep : this.steps) {
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
}
