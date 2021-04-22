package service.extension;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
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
			ClassDTO dto = new ClassDTO();
			// TODO Ignore nested classes
			List<ClassOrInterfaceDeclaration> list = unit.findAll(ClassOrInterfaceDeclaration.class);
			if (list.size() == 0) {
				return;
			}
			ClassOrInterfaceDeclaration decl = unit.findAll(ClassOrInterfaceDeclaration.class).get(0);
//			ClassOrInterfaceDeclaration decl = unit.findAll(ClassOrInterfaceDeclaration.class).stream()
//					.filter(ClassOrInterfaceDeclaration::isNestedType).findAny().get();
			// add class
			LOG.debug("Inspect class: " + decl.getFullyQualifiedName());
			dto.setJavaClass(decl);
			dto.setFullName(decl.getFullyQualifiedName().toString());
			// add fields
			List<FieldDeclaration> fields = decl.findAll(FieldDeclaration.class);
			dto.setFields(fields);
			// add constructors
			List<ConstructorDeclaration> constructors = decl.findAll(ConstructorDeclaration.class);
			dto.setConstructors(constructors);
			// add methods
			List<MethodDeclaration> methods = decl.findAll(MethodDeclaration.class);
			dto.setMethods(methods);
			// add implements
			List<ClassOrInterfaceType> implementsList = new ArrayList<>(decl.getImplementedTypes());
			dto.setImplementations(implementsList);
			// add extends
			List<ClassOrInterfaceType> extendsList = new ArrayList<>(decl.getExtendedTypes());
			dto.setExtensions(extendsList);
			// add dto to the collection
			this.classes.add(dto);
		});
	}

}
