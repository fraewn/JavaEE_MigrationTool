package parser.utils;

import java.util.List;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;

public class AnnotationResolver {

	private static final String VALUE = "value";

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

	public static List<MemberValuePair> getParamaterList(AnnotationExpr annotationExpr) {
		return annotationExpr.findAll(MemberValuePair.class);
	}

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
