package service.extension;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import operations.LoaderService;
import operations.dto.ClassDTO;
import parser.LoadSourcesService;
import parser.LoadSourcesServiceImpl;

/**
 * Default method to load a defined project. Convert all classes to a
 * {@link ClassDTO}
 */
public class DefaultLoader extends LoaderService<Object, List<ClassDTO>> {

	private static final Logger LOG = Logger.getLogger(DefaultLoader.class);

	private List<ClassDTO> classes;

	public DefaultLoader() {
		this.classes = new ArrayList<>();
	}

	@Override
	public List<ClassDTO> loadProject(Object dto) {
		LoadSourcesService service = new LoadSourcesServiceImpl();
		service.loadSources(this.path);
		List<CompilationUnit> units = service.getAllCompilationUnits();
		units.forEach(unit -> {
			// Find all classes in file
			List<ClassOrInterfaceDeclaration> classList = unit.findAll(ClassOrInterfaceDeclaration.class);
			// if the file has no classes, skip further work
			if (classList.isEmpty()) {
				return;
			}
			// iterate all classes in file
			for (ClassOrInterfaceDeclaration decl : classList) {
				String classPath = decl.getFullyQualifiedName().get().toString();
				LOG.debug("Inspect class: " + classPath);
				// ignore the class if it is nested (inner class)
				if (decl.isInnerClass()) {
					LOG.debug("Ignoring class " + classPath + " since it's nested");
					continue;
				}
				ClassDTO classDTO = new ClassDTO();
				classDTO.setFullName(classPath);
				// read package and module declaration from unit (same for all classes in unit)
				unit.getPackageDeclaration().ifPresent(v -> classDTO.setPackageDeclaration(v.getNameAsString()));
				unit.getModule().ifPresent(v -> classDTO.setModuleDeclaration(v.getNameAsString()));
				// save imports
				classDTO.setImports(unit.getImports());
				// save class
				classDTO.setJavaClass(decl);
				// add interfaces the class implements
				classDTO.setImplementations(decl.getImplementedTypes());
				// add classes the class extends from
				classDTO.setExtensions(decl.getExtendedTypes());
				// add fields of class
				classDTO.setFields(decl.findAll(FieldDeclaration.class));
				// add constructors of class
				classDTO.setConstructors(decl.findAll(ConstructorDeclaration.class));
				// add methods of class
				classDTO.setMethods(decl.findAll(MethodDeclaration.class));
				// collect all annotations for a class
				classDTO.setAnnotationDeclarationList(decl.getAnnotations());
				// collect all type parameters
				classDTO.setTypeParameters(decl.getTypeParameters());
				// add dto to the collection
				this.classes.add(classDTO);
			}
		});
		return this.classes;
	}

}
