package command;

import java.util.SortedMap;
import java.util.TreeMap;

import org.kohsuke.args4j.Option;

import api.CommandStep;
import api.InterpreterService;
import api.LoaderService;
import api.ModelService;

public abstract class DefaultCommand extends AbstractCommand {

	@Option(name = "-loader", usage = "value for defining the loader service class")
	private String loader;

	@Option(name = "-model", usage = "value for defining the model service class")
	private String model;

	@Option(name = "-interpreter", usage = "value for defining the interpreter service class")
	private String interpreter;

	@Override
	protected SortedMap<String, Class<? extends CommandStep>> defineSteps() {
		SortedMap<String, Class<? extends CommandStep>> steps = new TreeMap<>();
		steps.put(this.loader == null ? "DefaultLoader" : this.loader, LoaderService.class);
		steps.put(this.model == null ? "DefaultModel" : this.model, ModelService.class);
		steps.put(this.interpreter == null ? "DefaultInterpreter" : this.interpreter, InterpreterService.class);
		return steps;
	}
}
