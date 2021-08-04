package operations;

import cmd.CommandLineParser;

/**
 * Abstraction of a processing step in the defined pipeline
 *
 * @param <I> Input Object
 * @param <O> Output Object
 */
public interface ProcessingStep<I, O> {
	/**
	 * Execute a process on the input object and generate the correct output
	 *
	 * @param input Input Object
	 * @return Output Object
	 */
	O process(I input);

	/**
	 * Set the command line arguments for the service
	 *
	 * @param args
	 */
	default void setCommandLineArguments(String[] args) {
		CommandLineParser.parse(args, this);
	}
}
