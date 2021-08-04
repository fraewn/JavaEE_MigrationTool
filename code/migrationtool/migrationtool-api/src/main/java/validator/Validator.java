package validator;

import java.util.Map;

import exceptions.MigrationToolArgumentException;

/**
 * Validator to check for errors in a configuration file
 */
public interface Validator {

	/**
	 * Is the configuration file always required
	 *
	 * @return bool
	 */
	boolean isOptional();

	/**
	 * Path to configuration file
	 *
	 * @return
	 */
	String configFile();

	/**
	 * Validates the configuration file
	 *
	 * @param cache current values in configuration file
	 * @throws MigrationToolArgumentException if there are errors
	 */
	void validate(Map<String, String> cache) throws MigrationToolArgumentException;
}
