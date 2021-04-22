package visitors;

import java.io.File;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import DTOs.MethodDependencyDTO;
import exceptions.MigrationToolInitException;

public class MethodCallResolver {
	public void findMethodDependencies(File file, List<ClassOrInterfaceDeclaration> classList,
			MethodDependencyDTO methodDependencyDTO) throws MigrationToolInitException

	{

		TypeSolver typeSolver = new ReflectionTypeSolver();
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);

		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);
		} catch (Exception e) {
			throw new MigrationToolInitException(e.getMessage());
		}

	}
}
