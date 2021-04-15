import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.List;

public class LoadClassesTest {
	public static void main(String[] args) {
		System.out.println("Starting Test");
		LoadClasses loadClasses = new LoadClasses();
		System.out.println("LoadClass was loaded");
		List<ClassOrInterfaceDeclaration> allClasses = loadClasses.listClasses("C:\\Users\\Administrator\\OneDrive\\Uni " +
			"Cloud\\Masterprojekt\\Implementierung\\automisedMigration\\JavaEE_MigrationTool\\code\\migrationtool\\migrationtool-core\\src\\main\\java");
		for(ClassOrInterfaceDeclaration decl: allClasses){
			System.out.println(decl.getFullyQualifiedName().toString());
		}
	}
}
