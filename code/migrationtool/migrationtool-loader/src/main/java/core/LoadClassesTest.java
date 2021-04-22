package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DTOs.MethodDependencyDTO;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import resolvers.MethodCallResolver;

public class LoadClassesTest {
	public static void main(String[] args) throws IOException {
		String root = "C:\\Users\\Administrator\\Desktop\\RatingMgmt";
		System.out.println("path: " + root);

		// methods
		List<MethodDependencyDTO> methodDependencyDTOList = new ArrayList<>();
		VoidVisitor<List<MethodDependencyDTO>> methodDependencyCollector = new MethodCallResolver();

		System.out.println("loading files");
		LoadSourceFiles files = new LoadSourceFiles(root);
		System.out.println("getting compilation units");
		files.getAllCompilationUnits().forEach(x -> {
			// System.out.println(x.findAll(ClassOrInterfaceDeclaration.class));
			//System.out.println("searching for methods in cu " + x.toString());
			methodDependencyCollector.visit(x, methodDependencyDTOList);
		});
		System.out.println("printing method collection results:");
		System.out.println("size of method dependency list: " + methodDependencyDTOList.size());
		methodDependencyDTOList.forEach(dto -> System.out.println("Class: " + dto.getMethodCallingClass() + " uses method: "
		+ dto.getMce() + " from class: " + dto.getClassWithMethod()));
	}
}
