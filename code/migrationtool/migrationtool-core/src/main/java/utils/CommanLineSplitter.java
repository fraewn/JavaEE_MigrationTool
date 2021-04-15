package utils;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

public class CommanLineSplitter {

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

	public static String[] undefinedArgs(String[] args, CmdLineParser parser) {
		String[] tmp = definedArgs(args, parser);
		List<String> res = new ArrayList<>();
		for (String s : args) {
			boolean defined = false;
			for (String def : tmp) {
				if (def.equals(s)) {
					defined = true;
					break;
				}
			}
			if (!defined) {
				res.add(s);
			}
		}
		return res.toArray(new String[0]);
	}
}
