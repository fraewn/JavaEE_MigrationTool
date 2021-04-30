package service.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.type.TypeParameter;
import org.apache.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import operations.LoaderService;
import operations.dto.ClassDTO;
import operations.dto.GenericDTO;
import parser.LoadSourcesService;
import parser.LoadSourcesServiceImpl;

public class DefaultLoader extends LoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoader.class);

	private List<ClassDTO> classes;

	public DefaultLoader() {
		this.classes = new ArrayList<>();
	}

	@Override
	public void setDTO(GenericDTO<?> dto) {
		// Nothing
	}

	@Override
	public GenericDTO<?> buildDTO() {
		return new GenericDTO<>(this.classes);
	}

	@Override
	public void loadProject() {
		LoadSourcesService service = new LoadSourcesServiceImpl();
		service.loadSources(this.path);
		List<CompilationUnit> units = service.getAllCompilationUnits();
		units.forEach(unit -> {
			ClassDTO classDTO = new ClassDTO();

			// read package and module declaration from unit (same for all classes in unit)
			String packageDeclaration = unit.getPackageDeclaration().toString();
			String moduleDeclaration = unit.getModule().toString();
			List<ImportDeclaration> imports = unit.getImports();

			// Find all classes in file
			List<ClassOrInterfaceDeclaration> classList = unit.findAll(ClassOrInterfaceDeclaration.class);
			// if the file has no classes, skip further work
			if (classList.size() == 0) {
				return;
			}

			// iterate all classes in file
			for (ClassOrInterfaceDeclaration decl : classList) {
				String classPath = decl.getFullyQualifiedName().toString();
				LOG.debug("Inspect class: " + classPath);

				// ignore the class if it is nested (inner class)
				if (decl.isInnerClass()){
					LOG.debug("Ignoring class " + classPath + " since it's nested");
					break;
				}

				// save package and module declaration as well as whole path
				classDTO.setFullName(classPath);
				classDTO.setPackageDeclaration(packageDeclaration);
				classDTO.setModuleDeclaration(moduleDeclaration);

				// save imports
				classDTO.setImports(imports);

				// save class
				classDTO.setJavaClass(decl);

				// add interfaces the class implements
				List<ClassOrInterfaceType> implementsList = decl.getImplementedTypes();
				classDTO.setImplementations(implementsList);
				// add classes the class extends from
				List<ClassOrInterfaceType> extendsList = decl.getExtendedTypes();
				classDTO.setExtensions(extendsList);

				// add fields of class
				List<FieldDeclaration> fields = decl.findAll(FieldDeclaration.class);
				classDTO.setFields(fields);
				// add constructors of class
				List<ConstructorDeclaration> constructors = decl.findAll(ConstructorDeclaration.class);
				classDTO.setConstructors(constructors);
				// add methods of class
				List<MethodDeclaration> methods = decl.findAll(MethodDeclaration.class);
				classDTO.setMethods(methods);

				// collect all annotations for a class
				List<AnnotationExpr> annotationExprs = decl.getAnnotations();
				classDTO.setAnnotationDeclarationList(annotationExprs);
				// collect all type parameters
				List<TypeParameter> typeParameterList = decl.getTypeParameters();
				classDTO.setTypeParameters(typeParameterList);

				// add dto to the collection
				this.classes.add(classDTO);
			}

// 			ClassOrInterfaceDeclaration decl = unit.findAll(ClassOrInterfaceDeclaration.class).get(0);
//			ClassOrInterfaceDeclaration decl = unit.findAll(ClassOrInterfaceDeclaration.class).stream()
//					.filter(ClassOrInterfaceDeclaration::isNestedType).findAny().get();
			// add class

		});
	}

}
