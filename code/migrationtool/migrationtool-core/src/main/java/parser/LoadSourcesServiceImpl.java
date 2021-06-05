package parser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import exceptions.MigrationToolRuntimeException;

public class LoadSourcesServiceImpl implements LoadSourcesService {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(LoadSourcesServiceImpl.class);
	/** Project root directory, containing all analyzed classes */
	private ProjectRoot rootDirectory;

	@Override
	public void loadSources(String path) {
		try {
			// load all dependency jars
			List<String> pathsToJars = new ArrayList<>();
			loadDependencyJars(path, pathsToJars);
			// build solver
			CombinedTypeSolver solver = new CombinedTypeSolver();
			// add java classes
			solver.add(new ReflectionTypeSolver());
			LOG.info("Searching for dependency jars in root folder");
			for (String pathJar : pathsToJars) {
				LOG.debug("Found dependency: " + pathJar);
				// add dependency jars
				solver.add(new JarTypeSolver(pathJar));
			}
			LOG.info(pathsToJars.size() + " dependencies found");
			JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
			ParserConfiguration conf = new ParserConfiguration();
			conf.setSymbolResolver(symbolSolver);
			// parse classes
			this.rootDirectory = new ParserCollectionStrategy(conf).collect(new File(path).toPath());
			LOG.info("Searching for source directories in root folder");
			for (SourceRoot source : this.rootDirectory.getSourceRoots()) {
				// add the analyzed project
				LOG.debug("Found root directory: " + source.getRoot().toString());
				solver.add(new JavaParserTypeSolver(source.getRoot()));
				source.tryToParse();
			}
			LOG.info("Parsed all classes");
		} catch (IOException e) {
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}

	/*
	 * Search for all .jar files recursively in a defined parent folder
	 */
	private void loadDependencyJars(String path, List<String> pathsToJars) {
		File file = new File(path);
		FileFilter filter = pathname -> pathname.isDirectory() || pathname.getName().endsWith(".jar");
		if (file.isDirectory() && (file.listFiles(filter) != null)) {
			for (File subFile : Objects.requireNonNull(file.listFiles(filter))) {
				if (subFile.isDirectory()) {
					loadDependencyJars(subFile.getPath(), pathsToJars);
				} else {
					pathsToJars.add(subFile.getPath());
				}
			}
		} else {
			if (file.getName().endsWith(".jar")) {
				pathsToJars.add(file.getPath());
			} else {
				LOG.info(file + " is an empty directory");
			}
		}
	}

	@Override
	public List<CompilationUnit> getAllCompilationUnits() {
		return allLoadedUnits();
	}

	/*
	 * convert the entries of the source directory to compilation units (javaparser
	 * class representing a class)
	 */
	private List<CompilationUnit> allLoadedUnits() {
		List<CompilationUnit> cu = new ArrayList<>();
		for (SourceRoot source : this.rootDirectory.getSourceRoots()) {
			cu.addAll(source.getCompilationUnits());
		}
		return cu;
	}

}
