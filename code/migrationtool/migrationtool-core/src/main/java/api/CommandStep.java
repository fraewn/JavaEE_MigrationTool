package api;

/**
 * Interface to define a service of the migrationtool
 */
public interface CommandStep {
	/**
	 * Set the command line arguments for the service
	 * 
	 * @param args
	 */
	void setCommandLineArguments(String[] args);

	/**
	 * Execute the service
	 */
	void execute();
}
