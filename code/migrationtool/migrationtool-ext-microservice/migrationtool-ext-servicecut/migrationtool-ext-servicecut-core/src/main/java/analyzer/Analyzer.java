package analyzer;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.ModelRepresentation;
import operations.dto.AstDTO;

/**
 * Filters the Ast-Tree for the specified definition of an entity/useCase.
 * Generates the model.
 */
public interface Analyzer {

	/** Location of configurations file */
	String FILE_NAME = "servicesnipper/servicesnipper.properties";

	/**
	 * Parse all classes to the model representation
	 *
	 * @param classes all classes from the monolith
	 */
	void analyze(List<AstDTO> classes);

	/**
	 * @return the model
	 */
	ModelRepresentation getModel();

	/**
	 * @return the entities
	 */
	List<AstDTO> getEntities();

	/**
	 * @return the useCases
	 */
	Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> getUseCases();
}
