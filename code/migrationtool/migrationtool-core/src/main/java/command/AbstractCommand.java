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

	protected abstract SortedMap<Class<? extends CommandStep>, String> defineSteps();

	private void loadSteps() {
		SortedMap<Class<? extends CommandStep>, String> list = defineSteps();
		for (Map.Entry<Class<? extends CommandStep>, String> step : list.entrySet()) {
			if (step == null) {
				throw new MigrationToolInitException("No Implementation declared");
			}
			boolean foundImpl = false;
			ServiceLoader.load(step.getKey()).forEach(plugin -> {
				if (step.getValue().equals(SERVICE_EXTENSION_PACKAGE + "." + plugin.getClass().getSimpleName())) {
					this.steps.add(plugin);
				}
			});
			if (!foundImpl) {
				throw new MigrationToolInitException("No Implementation found");
			}
		}
	}

	public void run() {
		LOG.info("Execute command " + getName());
		LOG.info("Initialize...");
		beforeInitialization();
		loadSteps();
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
