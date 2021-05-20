package parser;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Describes the steps to load all relevant information of a project
 */
public interface LoadSourcesService {

	/**
	 * Load a project and analyze it by the given path
	 * 
	 * @param path path to project (parent folder is sufficient)
	 */
	void loadSources(String path);

	/**
	 * Gets the list of all analyzed classes
	 * 
	 * @return List of classes
	 */
	List<CompilationUnit> getAllCompilationUnits();
}
