package parser.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a specific type/method
 */
public class UsedTypeVisitor extends GenericVisitorAdapter<Boolean, Void> {

	/** searched definition */
	private String searchedEntity;
	/** searched methods of the object */
	private List<String> searchedMethods;

	public UsedTypeVisitor(String searchedEntity, String... searchedMethods) {
		this.searchedEntity = searchedEntity;
		this.searchedMethods = new ArrayList<>(Arrays.asList(searchedMethods));
	}

	@Override
	public Boolean visit(MethodCallExpr n, Void arg) {
		if (this.searchedMethods.contains(n.getNameAsString())) {
			Expression exp = n.getScope().orElse(null);
			if (exp != null) {
				if (TypeResolver.getFullyQualifiedName(exp.calculateResolvedType()).equals(this.searchedEntity)) {
					if (this.searchedMethods == null) {
						return Boolean.TRUE;
					}
					for (String method : this.searchedMethods) {
						boolean temp = Optional
								.ofNullable(n.accept(new UsedMethodVisitor(this.searchedEntity, method), null))
								.orElse(false);
						if (temp) {
							return Boolean.TRUE;
						}
					}
				}
			}
		}
		return super.visit(n, arg);
	}
}
