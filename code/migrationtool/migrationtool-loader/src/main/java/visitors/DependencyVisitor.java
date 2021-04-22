package visitors;

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class DependencyVisitor extends GenericVisitorAdapter<Boolean, String> {

	private List<String> blackListAnnotations;

	public DependencyVisitor(String... blacklistedAnnotations) {
		this.blackListAnnotations = Arrays.asList(blacklistedAnnotations);
	}

	@Override
	public Boolean visit(FieldDeclaration n, String arg) {
		for (VariableDeclarator variable : n.getVariables()) {
			// Print the field's class typr
			ResolvedReferenceType type = variable.getType().resolve().asReferenceType();
		}
		for (AnnotationExpr expr : n.getAnnotations()) {
			System.out.println(expr.getNameAsString());
//			System.out.println(expr.);
		}
		return super.visit(n, arg);
	}
}
