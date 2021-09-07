package parser.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithVariables;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;

import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a assign operation
 */
public class AssignVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched target, fully qualified name */
	private String searchedTarget;
	/** type of object, e.g. new XX() */
	private boolean objectCreation;
	/** target type of searched annotation */
	private Operator operator;

	public AssignVisitor(String searchedTarget, boolean objectCreation) {
		this.searchedTarget = searchedTarget;
		this.objectCreation = objectCreation;
		if (objectCreation) {
			this.operator = Operator.ASSIGN;
		}
	}

	public AssignVisitor(String searchedTarget, boolean objectCreation, Operator operator) {
		this.searchedTarget = searchedTarget;
		this.objectCreation = objectCreation;
		this.operator = operator;
	}

	@Override
	public Boolean visit(AssignExpr n, Void container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, Void arg) {
		if (this.objectCreation && check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, arg);
	}

	@Override
	public Boolean visit(VariableDeclarationExpr n, Void arg) {
		if (this.objectCreation && check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, arg);
	}

	private <T extends NodeWithVariables<?>> boolean check(T n) {
		for (VariableDeclarator var : n.getVariables()) {
			if (TypeResolver.getFullyQualifiedName(var.resolve().getType()).equals(this.searchedTarget)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private boolean check(AssignExpr obj) {
		// Check operator
		if (!obj.getOperator().equals(this.operator)) {
			return Boolean.FALSE;
		}
		// Check Target
		if (!obj.getTarget().isFieldAccessExpr()) {
			return Boolean.FALSE;
		}
		ResolvedType type = obj.getTarget().asFieldAccessExpr().resolve().getType();
		String tmp = TypeResolver.getFullyQualifiedName(type);
		if (!tmp.equals(this.searchedTarget)) {
			return Boolean.FALSE;
		}
		// Check Assignment
		if (!obj.getValue().isObjectCreationExpr() && this.objectCreation) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
