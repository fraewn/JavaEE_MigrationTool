package core;

import java.io.IOException;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class LoadClassesTest {
	public static void main(String[] args) throws IOException {
		String root = "C:\\Users\\Rene\\git\\migrationtool\\JavaEE_MigrationTool\\code\\migrationtool";
		LoadSourceFiles files = new LoadSourceFiles(root);
		files.getAllCompilationUnits().forEach(x -> {
			System.out.println(x.findAll(ClassOrInterfaceDeclaration.class));
		});
	}
}
