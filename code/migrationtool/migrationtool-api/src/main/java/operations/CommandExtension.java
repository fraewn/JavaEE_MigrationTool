package operations;

import java.util.Map;

import org.kohsuke.args4j.Option;

/**
 * Class to define your own process of the migrationtool
 */
public abstract class CommandExtension {

	@Option(name = "-loader", usage = "value for defining the loader service class")
	protected String loader;

	@Option(name = "-model", usage = "value for defining the model service class")
	protected String model;

	@Option(name = "-interpreter", usage = "value for defining the interpreter service class")
	protected String interpreter;

	/**
	 * Define the name of the command
	 *
	 * @return command name
	 */
	public abstract String getName();

	/**
	 * Define the order of possible services for this command
	 *
	 * @param definedSteps sorted list
	 */
	public abstract void defineSteps(Map<String, Class<? extends CommandStep>> definedSteps);

	/**
	 * Process before Initialization
	 */
	public abstract void beforeInitialization();

	/**
	 * Process after Initialization
	 */
	public abstract void afterInitialization();

	/**
	 * Process after Execution
	 */
	public abstract void afterExecution();
}
