package service.extension;

import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import api.LoaderService;
import utils.CommandLineParser;

public class DefaultLoader implements LoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoader.class);

	@Option(name = "-path", required = true, usage = "value for defining the location of the class files")
	private String path;

	private List<ClassOrInterfaceDeclaration> allClasses;

	@Override
	public void setCommandLineArguments(String[] args) {
		CommandLineParser.parse(args, this);
	}

	@Override
	public void findClasses() {
		LOG.info("Defined Path of project:" + this.path);
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
