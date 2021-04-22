package command;

import java.util.Map;

import exceptions.MigrationToolInitException;
import operations.CommandExtension;
import operations.CommandStep;

public class ExtensionCommand extends AbstractCommand {

	/** reference to the extension */
	private CommandExtension commandExtension;

	public ExtensionCommand(CommandExtension commandExtension) {
		this.commandExtension = commandExtension;
		if (commandExtension == null) {
			throw new MigrationToolInitException("Extension is null");
		}
	}

	@Override
	public String getName() {
		return this.commandExtension.getName();
	}

	@Override
	public void defineSteps(Map<String, Class<? extends CommandStep>> definedSteps) {
		this.commandExtension.defineSteps(definedSteps);
	}

	@Override
	public void afterExecution() {
		this.commandExtension.afterExecution();
	}

	@Override
	public void beforeInitialization() {
		this.commandExtension.beforeInitialization();
	}

	@Override
	public void afterInitialization() {
		this.commandExtension.afterInitialization();
	}

	@Override
	protected Object getCmdObject() {
		return this.commandExtension;
	}
}
