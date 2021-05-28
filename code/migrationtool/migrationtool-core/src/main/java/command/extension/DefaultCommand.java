package command.extension;

import java.util.List;

import command.AbstractCommand;

/**
 * Basic Definition of a command with all three services
 */
public class DefaultCommand extends AbstractCommand {

	@Override
	public void defineSteps(List<String> definedSteps) {
		definedSteps.add((this.loader == null) || this.loader.isEmpty() ? "DefaultLoader" : this.loader);
		definedSteps.add((this.model == null) || this.model.isEmpty() ? "DefaultModel" : this.model);
		definedSteps.add(
				(this.interpreter == null) || this.interpreter.isEmpty() ? "DefaultInterpreter" : this.interpreter);
	}
}
