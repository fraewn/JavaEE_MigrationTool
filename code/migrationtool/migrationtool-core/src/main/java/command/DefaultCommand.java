package command;

import java.util.Map;

import org.kohsuke.args4j.Option;

import api.CommandStep;
import api.InterpreterService;
import api.LoaderService;
import api.ModelService;

/**
 * Basic Definition of a command with all three services
 */
public abstract class DefaultCommand extends AbstractCommand {

	@Option(name = "-loader", usage = "value for defining the loader service class")
	private String loader;

	@Option(name = "-model", usage = "value for defining the model service class")
	private String model;

	@Option(name = "-interpreter", usage = "value for defining the interpreter service class")
	private String interpreter;

	@Override
	protected void defineSteps(Map<String, Class<? extends CommandStep>> definedSteps) {
		definedSteps.put(this.loader == null ? "DefaultLoader" : this.loader, LoaderService.class);
		definedSteps.put(this.model == null ? "DefaultModel" : this.model, ModelService.class);
		definedSteps.put(this.interpreter == null ? "DefaultInterpreter" : this.interpreter, InterpreterService.class);
	}
}
