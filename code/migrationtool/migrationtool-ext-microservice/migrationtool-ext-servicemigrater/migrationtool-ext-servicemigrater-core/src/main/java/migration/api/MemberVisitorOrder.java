package migration.api;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

public class MemberVisitorOrder {

	public static void order(CompilationUnit unit) {
		unit.findFirst(ClassOrInterfaceDeclaration.class).get().getMembers()
				.sort((o1, o2) -> {
					if (o1.isFieldDeclaration() && !o2.isFieldDeclaration()) {
						return -1;
					}
					if (!o1.isFieldDeclaration() && o2.isFieldDeclaration()) {
						return 1;
					}
					if (o1.isFieldDeclaration() && o2.isFieldDeclaration()) {
						FieldDeclaration fieldDecl = (FieldDeclaration) o1;
						FieldDeclaration compare = (FieldDeclaration) o2;
						if (fieldDecl.isFinal() && !compare.isFinal()) {
							return -1;
						}
						if (!fieldDecl.isFinal() && compare.isFinal()) {
							return 1;
						}
						if (fieldDecl.isStatic() && !compare.isStatic()) {
							return -1;
						}
						if (!fieldDecl.isStatic() && compare.isStatic()) {
							return 1;
						}
						if (fieldDecl.getAnnotationByName("Id").isPresent()
								&& !compare.getAnnotationByName("Id").isPresent()) {
							return -1;
						}
						if (!fieldDecl.getAnnotationByName("Id").isPresent()
								&& compare.getAnnotationByName("Id").isPresent()) {
							return 1;
						}
						return fieldDecl.getVariable(0).getNameAsString()
								.compareToIgnoreCase(compare.getVariable(0).getNameAsString());
					}
					return 0;
				});
	}
}
