package validation;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolArgumentException;
import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import utils.PluginManager;
import utils.PropertiesLoader;
import validator.Validator;

/**
 * If there are validators configured, it is possible to check for configuration
 * problems before running the migrationtool process
 */
public class ConfigFileValidator {
	/** Logger */
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Check if there errors in the config files
	 */
	public static void checkAllConfigFiles() {
		List<Validator> validators = PluginManager.findPluginValidators();
		if (validators.isEmpty()) {
			return;
		}
		for (Validator validator : validators) {
			String filePath = validator.configFile();
			PropertiesLoader loader = new PropertiesLoader(filePath);
			try {
				loader.loadProps(validator.isOptional());
				Map<String, String> cache = loader.getCache();
				validator.validate(cache);
				LOG.info("Validation of " + filePath + " SUCCESSFULL");
			} catch (MigrationToolInitException e) {
				if (!validator.isOptional()) {
					throw new MigrationToolInitException(e.getMessage());
				}
			} catch (MigrationToolArgumentException e) {
				if (!validator.isOptional()) {
					throw new MigrationToolInitException(e.getMessage());
				}
				LOG.warn(e.getMessage());
			} catch (RuntimeException e) {
				throw new MigrationToolRuntimeException("Unexpected error: " + e.getMessage(), e);
			}
		}
	}
}
