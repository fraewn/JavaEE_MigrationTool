package parser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class TautologyVisitor extends GenericVisitorAdapter<Boolean, Void> {

	@Override
	public Boolean visit(MethodCallExpr n, Void arg) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visit(MethodDeclaration n, Void arg) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, Void arg) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visit(FieldDeclaration n, Void arg) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, Void arg) {
		return Boolean.TRUE;
	}
}
