package operations;

import cmd.CommandLineParser;

public interface ProcessingStep<I, O> {
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
