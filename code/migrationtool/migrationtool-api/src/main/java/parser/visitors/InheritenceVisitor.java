package parser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/**
 * Filter the AST-tree for a inheritence definition
 */
public class InheritenceVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched definition */
	private String searchedDefinition;

	public InheritenceVisitor(String searchedDefinition) {
		this.searchedDefinition = searchedDefinition;
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, Void arg) {
		for (ClassOrInterfaceType ex : n.getExtendedTypes()) {
			if (ex.resolve().getQualifiedName().equals(this.searchedDefinition)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, arg);
	}
}
