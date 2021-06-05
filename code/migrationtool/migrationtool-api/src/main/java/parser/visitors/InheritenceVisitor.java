package parser.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class InheritenceVisitor extends GenericVisitorAdapter<Boolean, ClassOrInterfaceDeclaration> {

	/** searched definition */
	private String searchedDefinition;

	public InheritenceVisitor(String searchedDefinition) {
		this.searchedDefinition = searchedDefinition;
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, ClassOrInterfaceDeclaration arg) {
		for (ClassOrInterfaceType ex : n.getExtendedTypes()) {
			if (ex.resolve().getQualifiedName().equals(this.searchedDefinition)) {
				return Boolean.TRUE;
			}
		}
		return super.visit(n, arg);
	}
}
