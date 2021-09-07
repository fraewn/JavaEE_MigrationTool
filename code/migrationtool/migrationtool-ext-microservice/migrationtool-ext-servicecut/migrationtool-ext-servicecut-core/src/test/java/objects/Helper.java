package objects;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import operations.dto.AstDTO;

public class Helper {

	public static List<AstDTO> buildAstDTO(CompilationUnit unit) {
		List<AstDTO> classes = new ArrayList<>();
		List<ClassOrInterfaceDeclaration> classList = unit.findAll(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration decl : classList) {
			String classPath = decl.getFullyQualifiedName().get().toString();
			if (decl.isInnerClass()) {
				continue;
			}
			AstDTO classDTO = new AstDTO();
			classDTO.setFullName(classPath);
			// save imports
			classDTO.setImports(unit.getImports());
			// read package and module declaration from unit
			unit.getPackageDeclaration().ifPresent(v -> classDTO.setPackageDeclaration(v.getNameAsString()));
			unit.getModule().ifPresent(v -> classDTO.setModuleDeclaration(v.getNameAsString()));
			// add dto to the collection
			classes.add(setClass(classDTO, decl));
		}
		return classes;
	}

	private static AstDTO setClass(AstDTO classDTO, ClassOrInterfaceDeclaration decl) {
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
}
