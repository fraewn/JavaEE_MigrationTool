package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import api.CommandStep;
import command.AbstractCommand;

/**
 * Utility class to load plugins of services or commands
 */
public class PluginManager {
	/** Package location of service plugins */
	private static final String SERVICE_EXTENSION_PACKAGE = "service.extension";
	/** Package location of command plugins */
	private static final String COMMAND_EXTENSION_PACKAGE = "command.extension";

	/**
	 * Find service plugins
	 * 
	 * @param plugin       Plugin Definition
	 * @param searchedImpl Plugin Implementation
	 * @return searched service
	 */
	public static CommandStep findPluginService(Class<? extends CommandStep> plugin, String searchedImpl) {
		List<CommandStep> res = new ArrayList<>();
		ServiceLoader.load(plugin).forEach(p -> {
			String path = SERVICE_EXTENSION_PACKAGE + "." + searchedImpl;
			if (path.equals(p.getClass().getName())) {
				res.add(p);
			}
		});
		return res.size() > 0 ? res.get(0) : null;
	}

	/**
	 * Find command plugins
	 * 
	 * @param plugin       command definition
	 * @param searchedImpl command implementation
	 * @return searched command
	 */
	public static AbstractCommand findPluginCommand(Class<? extends AbstractCommand> plugin, String searchedImpl) {
		List<AbstractCommand> res = new ArrayList<>();
		ServiceLoader.load(plugin).forEach(p -> {
			String path = COMMAND_EXTENSION_PACKAGE + "." + searchedImpl;
			if (path.equals(p.getClass().getName())) {
				res.add(p);
			}
		});
		return res.size() > 0 ? res.get(0) : null;
	}
}
