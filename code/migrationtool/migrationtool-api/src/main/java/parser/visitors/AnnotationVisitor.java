package parser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.enums.TargetTypes;

/**
 * Filter the AST-tree for a specific annotation
 */
public class AnnotationVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched annotation, fully qualified name */
	private String searchedAnnotation;
	/** target type of searched annotation */
	private TargetTypes type;

	public AnnotationVisitor(String searchedAnnotation, TargetTypes type) {
		this.searchedAnnotation = searchedAnnotation;
		this.type = type;
	}

	@Override
	public Boolean visit(MethodDeclaration n, Void container) {
		if (this.type.equals(TargetTypes.METHOD)) {
			if (check(n)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, Void container) {
		if (this.type.equals(TargetTypes.TYPE)) {
			if (check(n)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, Void container) {
		if (this.type.equals(TargetTypes.FIELD)) {
			if (check(n)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, Void container) {
		if (this.type.equals(TargetTypes.CONSTRUCTOR)) {
			if (check(n)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	private <T extends NodeWithAnnotations<?>> boolean check(T obj) {
		for (AnnotationExpr expr : obj.getAnnotations()) {
			String qualifiedName = expr.resolve().getQualifiedName();
			if (qualifiedName.equals(this.searchedAnnotation)) {
				return Boolean.TRUE;
			}
		}
		return false;
	}
}
