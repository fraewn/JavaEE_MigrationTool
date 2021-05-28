package command;

import java.util.List;

import exceptions.MigrationToolInitException;
import operations.CommandExtension;

/**
 * Wrapper class for a user defined comannd
 */
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
	public void defineSteps(List<String> definedSteps) {
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
