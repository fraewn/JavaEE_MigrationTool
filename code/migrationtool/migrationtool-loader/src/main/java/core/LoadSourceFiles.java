package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

public class LoadSourceFiles {

	private static final Logger LOG = Logger.getLogger(LoadSourceFiles.class);

	private ProjectRoot rootDirectory;

	public LoadSourceFiles(String path) {
		try {
			this.rootDirectory = new ParserCollectionStrategy().collect(new File(path).toPath());
			for (SourceRoot source : this.rootDirectory.getSourceRoots()) {
				source.tryToParse();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
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
