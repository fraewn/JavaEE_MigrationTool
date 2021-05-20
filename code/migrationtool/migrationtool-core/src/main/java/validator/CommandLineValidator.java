package validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

import cmd.CommandLineParser;
import core.Runner;
import operations.CommandExtension;
import operations.CommandStep;
import operations.InterpreterService;
import operations.LoaderService;
import operations.ModelService;
import utils.PluginManager;

/**
 * Utility class to list all possible options, currently provided by the
 * migrationtool
 */
public class CommandLineValidator {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(CommandLineParser.class);

	/**
	 * List all options of all services and commands
	 */
	public static void listAllPossibleArguments() {
		LOG.info("+++Arguments of the runner class+++");
		listAllPossibleArguments(new Runner());
		LOG.info("+++Arguments of all possible commands+++");
		List<CommandExtension> commands = new ArrayList<>();
		commands.addAll(PluginManager.findPluginCommands(CommandExtension.class));
		for (CommandExtension command : commands) {
			listAllPossibleArguments(command);
		}
		LOG.info("+++Arguments of all possible operations+++");
		List<CommandStep> allSteps = new ArrayList<>();
		allSteps.addAll(PluginManager.findPluginServices(LoaderService.class));
		allSteps.addAll(PluginManager.findPluginServices(ModelService.class));
		allSteps.addAll(PluginManager.findPluginServices(InterpreterService.class));
		for (CommandStep commandStep : allSteps) {
			listAllPossibleArguments(commandStep);
		}
	}

	/**
	 * List all possible arguments of the provided object
	 *
	 * @param o object
	 */
	public static void listAllPossibleArguments(Object o) {
		CmdLineParser parser = new CmdLineParser(o);
		if (parser.getOptions().size() != 0) {
			printOptionsOfClass(o, parser.getOptions());
		}
	}

	/*
	 * Method for the pretty printing in the outputstream
	 */
	@SuppressWarnings("rawtypes")
	private static void printOptionsOfClass(Object o, List<OptionHandler> options) {
		int maxLengthName = options.stream().mapToInt(x -> x.option.toString().length()).max().orElse(1);
		int maxLengthUsage = options.stream().filter(x -> x.option.usage() != null)
				.mapToInt(x -> x.option.usage().length()).max().orElse(1);
		int maxLengthDefault = options.stream().filter(x -> x.printDefaultValue() != null)
				.mapToInt(x -> x.printDefaultValue().length()).max().orElse(1);
		StringBuilder res = new StringBuilder(System.lineSeparator());
		for (OptionHandler<?> h : options) {
			StringBuilder sb = new StringBuilder("\t");
			sb.append(String.format("%-" + maxLengthName + "s", h.option.toString()));
			sb.append(String.format(" : %-" + maxLengthUsage + "s", h.option.usage() == null ? "" : h.option.usage()));
			sb.append(h.printDefaultValue() == null ? ""
					: String.format("  (default: %-" + maxLengthDefault + "s)", h.printDefaultValue()));
			sb.append(h.option.required() ? "  [required]" : "");
			sb.append(System.lineSeparator());
			res.append(sb.toString());
		}
		res.append("]");
		LOG.info("Arguments: " + o.getClass().getSimpleName() + " [ " + res.toString());

	}
}
