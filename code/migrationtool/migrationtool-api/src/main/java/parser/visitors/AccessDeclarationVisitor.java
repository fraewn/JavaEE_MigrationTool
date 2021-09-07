package parser.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAccessModifiers;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.enums.ModifierTypes;

/**
 * Filter the AST-tree for a specific declaration
 */
public class AccessDeclarationVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** Possible modifiers */
	private ModifierTypes type;

	public AccessDeclarationVisitor(ModifierTypes type) {
		this.type = type;
	}

	@Override
	public Boolean visit(MethodDeclaration n, Void container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, Void container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, Void container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, Void container) {
		if (check(n)) {
			return Boolean.TRUE;
		}
		return super.visit(n, container);
	}

	private <R extends Node, T extends NodeWithFinalModifier<R> & NodeWithStaticModifier<R> & NodeWithAccessModifiers<R>> boolean check(
			T obj) {
		if (this.type.equals(ModifierTypes.FINAL) && obj.isFinal()) {
			return true;
		}
		if (this.type.equals(ModifierTypes.STATIC) && obj.isStatic()) {
			return true;
		}
		if (this.type.equals(ModifierTypes.PROTECTED) && obj.isProtected()) {
			return true;
		}
		if (this.type.equals(ModifierTypes.PRIVATE) && obj.isPrivate()) {
			return true;
		}
		if (this.type.equals(ModifierTypes.PUBLIC) && obj.isPublic()) {
			return true;
		}
		return false;
	}
}
