package core;

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

public class LoadSourceFiles {

	private static final Logger LOG = Logger.getLogger(LoadSourceFiles.class);

	private ProjectRoot rootDirectory;

	public LoadSourceFiles(String path) {
		try {
			// load all dependency jars
			List<String> pathsToJars = new ArrayList<>();
			loadDependencyJars(path, pathsToJars);
			// build solver
			CombinedTypeSolver solver = new CombinedTypeSolver();
			solver.add(new ReflectionTypeSolver());
			for (String pathJar : pathsToJars) {
				solver.add(new JarTypeSolver(pathJar));
			}
			JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
			ParserConfiguration conf = new ParserConfiguration();
			conf.setSymbolResolver(symbolSolver);
			// parse classes
			this.rootDirectory = new ParserCollectionStrategy(conf).collect(new File(path).toPath());
			for (SourceRoot source : this.rootDirectory.getSourceRoots()) {
				solver.add(new JavaParserTypeSolver(source.getRoot()));
				source.tryToParse();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

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

	public List<CompilationUnit> getAllCompilationUnits() {
		return allLoadedUnits();
	}

	public CompilationUnit getSpecificCompilationUnit(String className) {
		// TODO
		return null;
	}

	private List<CompilationUnit> allLoadedUnits() {
		List<CompilationUnit> cu = new ArrayList<>();
		for (SourceRoot source : this.rootDirectory.getSourceRoots()) {
			cu.addAll(source.getCompilationUnits());
		}
		return cu;
	}
}
