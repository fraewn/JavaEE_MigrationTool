package parser.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.utils.Pair;

import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a specific return type
 */
public class MethodReturnValueVisitor extends GenericVisitorAdapter<Boolean, FieldDeclaration> {

	/** searched definition */
	private String searchedEntity;

	public MethodReturnValueVisitor(String searchedEntity) {
		this.searchedEntity = searchedEntity;
	}

	@Override
	public Boolean visit(MethodDeclaration n, FieldDeclaration arg) {
		ResolvedType type = n.resolve().getReturnType();
		String typeOfReturn = TypeResolver.getFullyQualifiedName(type);
		if (typeOfReturn == null) {
			return super.visit(n, arg);
		}
		if ((typeOfReturn != null) && typeOfReturn.equals(this.searchedEntity)) {
			return Boolean.TRUE;
		}
		// If there is a container object e.g. List, inspect type parameter
		if ((typeOfReturn != null) && type.isReferenceType()) {
			for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : type.asReferenceType()
					.getTypeParametersMap()) {
				String tmp = TypeResolver.getFullyQualifiedName(pair.b);
				if ((tmp != null) && tmp.equals(this.searchedEntity)) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, arg);
	}
}
