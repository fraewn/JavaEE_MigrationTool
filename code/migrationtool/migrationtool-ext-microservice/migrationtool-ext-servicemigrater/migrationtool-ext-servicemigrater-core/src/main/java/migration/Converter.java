package migration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import migration.model.data.UseCase;
import migration.model.erm.Entity;
import migration.model.serviceDefintion.ServiceRelation;
import operations.dto.AstDTO;

public abstract class Converter {

	protected ClassFactory factory;

	public Converter(ClassFactory factory) {
		this.factory = factory;
	}

	public abstract CompilationUnit createStartClass();

	public abstract CompilationUnit createEntity(Entity entity, AstDTO dto);

	public abstract CompilationUnit createRepository(Entity entity);

	public abstract List<CompilationUnit> createService(String qualifiedClassName, List<UseCase> methods);

	public abstract CompilationUnit publishLanguage(String currentService, ServiceRelation relation,
			Set<Entity> entities);

	public abstract void processAllClasses(List<CompilationUnit> allClasses);

	public abstract Map<String, String> generateSpecificFiles();
}
