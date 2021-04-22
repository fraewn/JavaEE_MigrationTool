package parser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import data.TargetTypes;

/**
 * Filter the AST-tree for a specific annotation
 */
public class AnnotationVisitor extends GenericVisitorAdapter<Boolean, AnnotationExpr> {

	private String searchedAnnotation;
	private TargetTypes type;

	public AnnotationVisitor(String searchedAnnotation, TargetTypes type) {
		this.searchedAnnotation = searchedAnnotation;
		this.type = type;
	}

	@Override
	public Boolean visit(MethodDeclaration n, AnnotationExpr container) {
		if (this.type.equals(TargetTypes.METHOD)) {
			for (AnnotationExpr expr : n.getAnnotations()) {
				String qualifiedName = expr.resolve().getQualifiedName();
				if (qualifiedName.equals(this.searchedAnnotation)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, AnnotationExpr container) {
		if (this.type.equals(TargetTypes.TYPE)) {
			for (AnnotationExpr expr : n.getAnnotations()) {
				String qualifiedName = expr.resolve().getQualifiedName();
				if (qualifiedName.equals(this.searchedAnnotation)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, AnnotationExpr container) {
		if (this.type.equals(TargetTypes.FIELD)) {
			for (AnnotationExpr expr : n.getAnnotations()) {
				String qualifiedName = expr.resolve().getQualifiedName();
				if (qualifiedName.equals(this.searchedAnnotation)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, AnnotationExpr container) {
		if (this.type.equals(TargetTypes.CONSTRUCTOR)) {
			for (AnnotationExpr expr : n.getAnnotations()) {
				String qualifiedName = expr.resolve().getQualifiedName();
				if (qualifiedName.equals(this.searchedAnnotation)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, container);
	}
}
