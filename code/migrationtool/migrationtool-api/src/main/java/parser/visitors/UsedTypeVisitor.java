package parser.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import parser.utils.TypeResolver;

public class UsedTypeVisitor extends GenericVisitorAdapter<Boolean, FieldDeclaration> {

	/** searched definition */
	private String searchedEntity;

	private List<String> searchedMethods;

	public UsedTypeVisitor(String searchedEntity, String... searchedMethods) {
		this.searchedEntity = searchedEntity;
		this.searchedMethods = new ArrayList<>(Arrays.asList(searchedMethods));
	}

	@Override
	public Boolean visit(MethodCallExpr n, FieldDeclaration arg) {
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
