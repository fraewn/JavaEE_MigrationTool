package parser.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import data.ModifierTypes;

/**
 * Filter the AST-tree for a specific declaration
 */
public class AccessDeclarationVisitor extends GenericVisitorAdapter<Boolean, String> {

	/** Possible modifiers */
	private ModifierTypes type;

	public AccessDeclarationVisitor(ModifierTypes type) {
		this.type = type;
	}

	@Override
	public Boolean visit(MethodDeclaration n, String container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, String container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, String container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, String container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	private <R extends Node, T extends NodeWithFinalModifier<R> & NodeWithStaticModifier<R>> boolean check(T obj) {
		if (this.type.equals(ModifierTypes.FINAL) && obj.isFinal()) {
			return true;
		}
		if (this.type.equals(ModifierTypes.STATIC) && obj.isStatic()) {
			return true;
		}
		return false;
	}
}
