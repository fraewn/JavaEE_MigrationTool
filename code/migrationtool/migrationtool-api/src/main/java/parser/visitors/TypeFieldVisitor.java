package parser.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;

import data.DefinitionTypes;

/**
 * Filter the AST-tree for a specific type
 */
public class TypeFieldVisitor extends GenericVisitorAdapter<Boolean, AnnotationExpr> {

	/** searched java type */
	private DefinitionTypes type;

	public TypeFieldVisitor(DefinitionTypes type) {
		this.type = type;
	}

	@Override
	public Boolean visit(FieldDeclaration n, AnnotationExpr container) {
		for (VariableDeclarator expr : n.getVariables()) {
			ResolvedType type = expr.resolve().getType();
			if (this.type.equals(DefinitionTypes.PRIMITIVE) && type.isPrimitive()) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}
}
