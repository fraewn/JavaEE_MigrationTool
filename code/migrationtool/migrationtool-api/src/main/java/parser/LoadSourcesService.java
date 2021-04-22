package parser;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public interface LoadSourcesService {

	void loadSources(String path);

	List<CompilationUnit> getAllCompilationUnits();
}
