package core;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class LoadClassesTest {
	public static void main(String[] args) {
		System.out.println("Starting Test");
		LoadSourceFiles loadClasses = new LoadSourceFiles();
		System.out.println("LoadClass was loaded");
		List<ClassOrInterfaceDeclaration> allClasses = loadClasses
				.listClasses("C:\\Users\\Rene\\git\\migrationtool\\JavaEE_MigrationTool\\code\\migrationtool");
		for (ClassOrInterfaceDeclaration decl : allClasses) {
			System.out.println(decl);
		}
	}
}
