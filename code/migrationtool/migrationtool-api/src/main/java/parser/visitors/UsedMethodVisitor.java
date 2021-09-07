package parser.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a specific method
 */
public class UsedMethodVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched target entity */
	private String searchedEntity;
	/** searched method of entity */
	private String searchedMethodName;
	/** method arguments of searched method */
	private List<String> searchedMethodArguments;
	/** inspect method arguments */
	private boolean search;

	public UsedMethodVisitor(String searchedEntity, String searchedMethodName) {
		this.searchedEntity = searchedEntity;
		this.searchedMethodName = searchedMethodName;
	}

	public UsedMethodVisitor(String searchedEntity, String searchedMethodName, boolean search,
			String... searchedMethodArguments) {
		this(searchedEntity, searchedMethodName);
		this.search = search;
		this.searchedMethodArguments = new ArrayList<>(Arrays.asList(searchedMethodArguments));
	}

	@Override
	public Boolean visit(MethodCallExpr n, Void arg) {
		if (n.getNameAsString().equals(this.searchedMethodName)) {
			Expression scope = n.getScope().orElse(null);
			if (scope != null) {
				String typeOfScope = TypeResolver.getFullyQualifiedName(scope.calculateResolvedType());
				if ((typeOfScope != null) && typeOfScope.equals(this.searchedEntity)) {
					// check arguments
					if (this.searchedMethodArguments == null) {
						return Boolean.TRUE;
					}
					if (((this.searchedMethodArguments != null) && !this.search)
							&& (this.searchedMethodArguments.size() == n.getArguments().size())) {
						boolean sameArgs = true;
						for (int i = 0; i < this.searchedMethodArguments.size(); i++) {
							String typeOfArg = TypeResolver
									.getFullyQualifiedName(n.getArgument(i).calculateResolvedType());
							if (!this.searchedMethodArguments.get(i).equals(typeOfArg)) {
								sameArgs = false;
								break;
							}
						}
						if (sameArgs) {
							return Boolean.TRUE;
						}
					}
					if ((this.searchedMethodArguments != null) && this.search) {
						for (Expression e : n.getArguments()) {
							String typeOfArg = TypeResolver.getFullyQualifiedName(e.calculateResolvedType());
							if (this.searchedMethodArguments.contains(typeOfArg)) {
								return Boolean.TRUE;
							}
							if (e.calculateResolvedType().isReferenceType()) {
								typeOfArg = TypeResolver
										.getFullyQualifiedName(e.calculateResolvedType().asReferenceType());
								if (this.searchedMethodArguments.contains(typeOfArg)) {
									return Boolean.TRUE;
								}
							}
						}
					}
				}
			}
		}
		return super.visit(n, arg);
	}
}
