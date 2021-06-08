package rules.definitions;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class TautologyVisitor extends GenericVisitorAdapter<Boolean, FieldDeclaration> {

	@Override
	public Boolean visit(MethodCallExpr n, FieldDeclaration arg) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visit(MethodDeclaration n, FieldDeclaration arg) {
		return Boolean.TRUE;
	}
}
