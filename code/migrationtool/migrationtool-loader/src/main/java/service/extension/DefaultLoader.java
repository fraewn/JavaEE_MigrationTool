package service.extension;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import api.LoaderService;
import utils.CommanLineSplitter;

public class DefaultLoader implements LoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoader.class);

	@Option(name = "-path", usage = "value for defining the location of the class files")
	private String path;

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

	@Override
	public void findClasses() {
		LOG.info(this.path);
	}

	@Override
	public void defineVisitors() {
		LOG.info(this.path);
	}

	@Override
	public void saveVisitorResult() {
		LOG.info(this.path);
	}
}
