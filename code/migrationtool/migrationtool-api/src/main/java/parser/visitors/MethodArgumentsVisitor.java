package parser.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.enums.ArgumentPosition;
import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a method argument
 */
public class MethodArgumentsVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched definition */
	private String searchedEntity;
	/** position of the argument */
	private ArgumentPosition pos;

	public MethodArgumentsVisitor(String searchedEntity, ArgumentPosition pos) {
		this.searchedEntity = searchedEntity;
		this.pos = pos;
	}

	@Override
	public Boolean visit(MethodDeclaration n, Void arg) {
		if (this.pos.equals(ArgumentPosition.DECLARATION)) {
			for (Parameter param : n.getParameters()) {
				String typeOfParam = TypeResolver.getFullyQualifiedName(param.resolve().getType());
				if (typeOfParam.equals(this.searchedEntity)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, arg);
	}

	@Override
	public Boolean visit(MethodCallExpr n, Void arg) {
		if (this.pos.equals(ArgumentPosition.CALL_EXPRESSION)) {
			for (Expression exp : n.getArguments()) {
				try {
					String typeOfParam = TypeResolver.getFullyQualifiedName(exp.calculateResolvedType());
					if (typeOfParam.equals(this.searchedEntity)) {
						return Boolean.TRUE;
					}
				} catch (Exception e) {
					// dirty; some expressions are not resolvable
				}
			}
		}
		return super.visit(n, arg);
	}
}
