package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DTOs.ClassDTO;
import DTOs.MethodDependencyDTO;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import visitors.MethodCallResolver;

public class LoadClassesTest {
	public static void main(String[] args) throws IOException {
		String projectRoot = "C:\\Users\\Administrator\\Desktop\\RatingMgmt";

		System.out.println("path: " + projectRoot);

		// methods
		VoidVisitor<List<MethodDependencyDTO>> methodDependencyCollector = new MethodCallResolver();

		// load java project data
		System.out.println("loading files");
		LoadSourceFiles files = new LoadSourceFiles(projectRoot);

		// extract java project data
		// create DTO to save extracted data in
		List<ClassDTO> loadedClasses = new ArrayList<>();

		// schleife die für jede cu die infos in ein Klassen DTO tut und es in die Liste packt
		System.out.println("creating classDTOs for each compilation Unit");
		files.getAllCompilationUnits().forEach(compilationUnit -> {
			for (ClassOrInterfaceDeclaration classOrInterfaceDeclaration : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
				// create class dto
				ClassDTO classDTO = new ClassDTO();

				// add class
				classDTO.setJavaClass(classOrInterfaceDeclaration);
				classDTO.setFullName(classOrInterfaceDeclaration.getFullyQualifiedName().toString());

				// add methods for each class
				List<MethodDeclaration> methodDeclarationList = new ArrayList<>();
				for(MethodDeclaration methodDeclaration: classOrInterfaceDeclaration.findAll(MethodDeclaration.class)){
					methodDeclarationList.add(methodDeclaration);
				}

				// add annotations for each class
				List<AnnotationDeclaration> annotationDeclarationList = new ArrayList<>();
				for(AnnotationDeclaration annotationDeclaration: classOrInterfaceDeclaration.findAll(AnnotationDeclaration.class)){
					annotationDeclarationList.add(annotationDeclaration);
				}

				// add constructors for each class
				List<ConstructorDeclaration> constructorDeclarationList = new ArrayList<>();
				for(ConstructorDeclaration constructorDeclaration: classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)){
					constructorDeclarationList.add(constructorDeclaration);
				}

				// add attributes for each class
				// TODO

				// add implements for each class
				List<ClassOrInterfaceType> implementsDependencyList = new ArrayList<>();
				implementsDependencyList.addAll(classOrInterfaceDeclaration.getImplementedTypes());

				// add extends for each class
				List<ClassOrInterfaceType> extendsDependencyList = new ArrayList<>();
				extendsDependencyList.addAll(classOrInterfaceDeclaration.getExtendedTypes());

				// add classDTO to the collection
				System.out.println(classDTO.getFullName() + classDTO.getAnnotationExprList().size());
				loadedClasses.add(classDTO);
			}
		});
	}
}
