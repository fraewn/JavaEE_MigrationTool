package service.extension;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import operations.LoaderService;
import operations.dto.AstDTO;
import parser.LoadSourcesService;
import parser.LoadSourcesServiceImpl;

/**
 * Default method to load a defined project. Convert all classes to a
 * {@link AstDTO}
 */
public class DefaultLoader extends LoaderService<Object, List<AstDTO>> {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private List<AstDTO> classes;

	public DefaultLoader() {
		this.classes = new ArrayList<>();
	}

	@Override
	public List<AstDTO> loadProject(Object dto) {
		LoadSourcesService service = new LoadSourcesServiceImpl();
		service.loadSources(this.path);
		List<CompilationUnit> units = service.getAllCompilationUnits();
		units.forEach(unit -> {
			// Find all classes in file
			List<ClassOrInterfaceDeclaration> classList = unit.findAll(ClassOrInterfaceDeclaration.class);
			// iterate all classes in file
			for (ClassOrInterfaceDeclaration decl : classList) {
				String classPath = decl.getFullyQualifiedName().get().toString();
				// ignore the class if it is nested (inner class)
				if (decl.isInnerClass()) {
					LOG.debug("Ignoring class " + classPath + " since it's nested");
					continue;
				}
				LOG.debug("Inspect class: " + decl.getNameAsString());
				AstDTO classDTO = new AstDTO();
				classDTO.setFullName(classPath);
				// save imports
				classDTO.setImports(unit.getImports());
				// read package and module declaration from unit
				unit.getPackageDeclaration().ifPresent(v -> classDTO.setPackageDeclaration(v.getNameAsString()));
				unit.getModule().ifPresent(v -> classDTO.setModuleDeclaration(v.getNameAsString()));
				// add dto to the collection
				this.classes.add(setClass(classDTO, decl));
			}
			// Find all enums in file
			List<EnumDeclaration> enumList = unit.findAll(EnumDeclaration.class);
			// if the file has no classes, skip further work
			for (EnumDeclaration decl : enumList) {
				String classPath = decl.getFullyQualifiedName().get().toString();
				// ignore the class if it is nested (inner class)
				if (decl.isNestedType()) {
					LOG.debug("Ignoring enum " + classPath + " since it's nested");
					continue;
				}
				LOG.debug("Inspect enum: " + decl.getNameAsString());
				AstDTO classDTO = new AstDTO();
				classDTO.setFullName(classPath);
				// save imports
				classDTO.setImports(unit.getImports());
				// read package and module declaration from unit
				unit.getPackageDeclaration().ifPresent(v -> classDTO.setPackageDeclaration(v.getNameAsString()));
				unit.getModule().ifPresent(v -> classDTO.setModuleDeclaration(v.getNameAsString()));
				// add dto to the collection
				this.classes.add(setEnum(classDTO, decl));
			}
		});
		return this.classes;
	}

	private AstDTO setClass(AstDTO classDTO, ClassOrInterfaceDeclaration decl) {
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
		// collect all enums
		classDTO.setEnums(decl.findAll(EnumDeclaration.class));
		return classDTO;
	}

	private AstDTO setEnum(AstDTO classDTO, EnumDeclaration decl) {
		// save class
		classDTO.setEnumClass(decl);
		// add interfaces the class implements
		classDTO.setImplementations(decl.getImplementedTypes());
		// add fields of class
		classDTO.setFields(decl.findAll(FieldDeclaration.class));
		// add constructors of class
		classDTO.setConstructors(decl.findAll(ConstructorDeclaration.class));
		// add methods of class
		classDTO.setMethods(decl.findAll(MethodDeclaration.class));
		// collect all annotations for a class
		classDTO.setAnnotationDeclarationList(decl.getAnnotations());
		return classDTO;
	}
}
