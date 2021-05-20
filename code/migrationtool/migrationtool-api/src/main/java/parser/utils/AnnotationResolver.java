package parser.utils;

import java.util.List;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;

/**
 * Utils class to receive the values of a annotation
 */
public class AnnotationResolver {

	/** Default parameter used in single parameter annotations */
	private static final String VALUE = "value";

	/**
	 * Get the value of a single parameter annotation
	 *
	 * @param annotationExpr annotation
	 * @return value of the parameter
	 */
	public static Expression getValueParameter(AnnotationExpr annotationExpr) {
		Expression expression = getParamater(annotationExpr, VALUE);
		if (expression == null) {
			List<Expression> children = annotationExpr.findAll(Expression.class);
			if (!children.isEmpty()) {
				expression = children.get(0);
			}
		}
		return expression;
	}

	/**
	 * Get all annotation parameters
	 *
	 * @param annotationExpr annotation
	 * @return list of key/value pairs
	 */
	public static List<MemberValuePair> getParamaterList(AnnotationExpr annotationExpr) {
		return annotationExpr.findAll(MemberValuePair.class);
	}

	/**
	 * Get the value of specific parameter
	 *
	 * @param annotationExpr annotation
	 * @param parameterName  searched parameter name
	 * @return value of parameter
	 */
	public static Expression getParamater(AnnotationExpr annotationExpr, String parameterName) {
		List<MemberValuePair> children = annotationExpr.findAll(MemberValuePair.class);
		for (MemberValuePair memberValuePair : children) {
			if (parameterName.equals(memberValuePair.getNameAsString())) {
				return memberValuePair.getValue();
			}
		}
		return null;
	}
}
