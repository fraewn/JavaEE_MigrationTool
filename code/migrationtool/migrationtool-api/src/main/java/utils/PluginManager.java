package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import operations.CommandExtension;
import operations.ProcessingStep;
import operations.Validator;

/**
 * Utility class to load plugins of services or commands
 */
public class PluginManager {
	/** Package location of service plugins */
	private static final String SERVICE_EXTENSION_PACKAGE = "service.extension";
	/** Package location of command plugins */
	private static final String COMMAND_EXTENSION_PACKAGE = "command.extension";
	/** Package location of validator plugins */
	private static final String VALIDATOR_EXTENSION_PACKAGE = "validator.extension";

	/**
	 * Find service plugins
	 *
	 * @param plugin       Plugin Definition
	 * @param searchedImpl Plugin Implementation
	 * @return searched service
	 */
	public static ProcessingStep<?, ?> findPluginService(String searchedImpl) {
		List<ProcessingStep<?, ?>> res = new ArrayList<>();
		ServiceLoader.load(ProcessingStep.class).forEach(p -> {
			String path = SERVICE_EXTENSION_PACKAGE + "." + searchedImpl;
			if (path.equals(p.getClass().getName())) {
				res.add(p);
			}
		});
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * Find service plugins
	 *
	 * @param plugin Plugin Definition
	 *
	 * @return searched services
	 */
	public static List<ProcessingStep<?, ?>> findPluginServices() {
		List<ProcessingStep<?, ?>> res = new ArrayList<>();
		ServiceLoader.load(ProcessingStep.class).forEach(p -> {
			res.add(p);
		});
		return res;
	}

	/**
	 * Find command plugins
	 *
	 * @param plugin       command definition
	 * @param searchedImpl command implementation
	 * @return searched command
	 */
	public static CommandExtension findPluginCommand(Class<? extends CommandExtension> plugin, String searchedImpl) {
		List<CommandExtension> res = new ArrayList<>();
		ServiceLoader.load(plugin).forEach(p -> {
			String path = COMMAND_EXTENSION_PACKAGE + "." + searchedImpl;
			if (path.equals(p.getClass().getName())) {
				res.add(p);
			}
		});
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * Find command plugins
	 *
	 * @param plugin command definition
	 * @return searched command
	 */
	public static List<CommandExtension> findPluginCommands(Class<? extends CommandExtension> plugin) {
		List<CommandExtension> res = new ArrayList<>();
		ServiceLoader.load(plugin).forEach(p -> {
			res.add(p);
		});
		return res;
	}

	/**
	 * Find validator plugins
	 *
	 * @param plugin Plugin Definition
	 *
	 * @return searched validators
	 */
	public static List<Validator> findPluginValidators() {
		List<Validator> res = new ArrayList<>();
		ServiceLoader.load(Validator.class).forEach(p -> {
			res.add(p);
		});
		return res;
	}

	/**
	 * Find validator plugin
	 *
	 * @param plugin       Plugin Definition
	 * @param searchedImpl Plugin Implementation
	 * @return searched validtor
	 */
	public static Validator findPluginValidator(String searchedImpl) {
		List<Validator> res = new ArrayList<>();
		ServiceLoader.load(Validator.class).forEach(p -> {
			String path = VALIDATOR_EXTENSION_PACKAGE + "." + searchedImpl;
			if (path.equals(p.getClass().getName())) {
				res.add(p);
			}
		});
		return res.size() > 0 ? res.get(0) : null;
	}
}
