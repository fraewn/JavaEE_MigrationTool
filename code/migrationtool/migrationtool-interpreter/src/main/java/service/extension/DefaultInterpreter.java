package service.extension;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import api.InterpreterService;
import utils.CommanLineSplitter;

public class DefaultInterpreter implements InterpreterService {

	private static final Logger LOG = Logger.getLogger(DefaultInterpreter.class);

	@Override
	public void setCommandLineArguments(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			// parse the arguments.
			parser.parseArgument(CommanLineSplitter.definedArgs(args, parser));
		} catch (CmdLineException e) {
			// this will report an error message.
			LOG.error(e.getMessage());
			LOG.error("java SampleMain [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
		}
	}
}
