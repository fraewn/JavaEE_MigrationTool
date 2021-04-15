package service.extension;

import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import api.LoaderService;
import core.LoadSourceFiles;
import exceptions.MigrationToolInitException;
import utils.CommandLineParser;
import utils.CommandLineSplitter;

public class DefaultLoader implements LoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoader.class);

	@Option(name = "-path", usage = "value for defining the location of the class files")
	private String path;

	private List<ClassOrInterfaceDeclaration> allClasses;

	@Override
	public void setCommandLineArguments(String[] args) {
		CmdLineParser parser = CommandLineParser.parse(args, this);
		if (CommandLineSplitter.undefinedArgs(args, parser) != null) {
			throw new MigrationToolInitException("arguments not valid");
		}
	}

	@Override
	public void findClasses() {
		LOG.info("Defined Path of project:" + this.path);
		this.allClasses = LoadSourceFiles.listClasses(this.path);
		LOG.info("Load complete");
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
