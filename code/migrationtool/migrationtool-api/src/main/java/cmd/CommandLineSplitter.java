package cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

/**
 * Utility class to seperate the list of arguments in defined and undefined
 */
public class CommandLineSplitter {

	/**
	 * Get all arguments which corresponds to the class
	 *
	 * @param args   list of arguments
	 * @param parser used parser
	 * @return defined arguments
	 */
	public static String[] definedArgs(String[] args, CmdLineParser parser) {
		List<String> res = new ArrayList<>();
		for (OptionHandler<?> o : parser.getOptions()) {
			String option = o.option.toString();
			for (String s : args) {
				if (s.startsWith(option)) {
					res.add(s);
				}
			}
		}
		return res.toArray(new String[0]);
	}

	/**
	 * Get all arguments which do not corresponds to the class
	 *
	 * @param args   list of arguments
	 * @param parser used parser
	 * @return undefined arguments
	 */
	public static String[] undefinedArgs(String[] args, CmdLineParser parser) {
		List<String> defined = new ArrayList<>(Arrays.asList(definedArgs(args, parser)));
		List<String> res = new ArrayList<>();
		for (String param : args) {
			if (!defined.contains(param)) {
				res.add(param);
			}
		}
		return res.toArray(new String[0]);
	}
}
