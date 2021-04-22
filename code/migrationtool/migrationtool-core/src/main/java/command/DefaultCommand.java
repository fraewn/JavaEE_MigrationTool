package command;

import java.util.Map;

import operations.CommandStep;
import operations.InterpreterService;
import operations.LoaderService;
import operations.ModelService;

/**
 * Basic Definition of a command with all three services
 */
public class DefaultCommand extends AbstractCommand {

	@Override
	public void defineSteps(Map<String, Class<? extends CommandStep>> definedSteps) {
		definedSteps.put(this.loader == null ? "DefaultLoader" : this.loader, LoaderService.class);
		definedSteps.put(this.model == null ? "DefaultModel" : this.model, ModelService.class);
		definedSteps.put(this.interpreter == null ? "DefaultInterpreter" : this.interpreter, InterpreterService.class);
	}
}
