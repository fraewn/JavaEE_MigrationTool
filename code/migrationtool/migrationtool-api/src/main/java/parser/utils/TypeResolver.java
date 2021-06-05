package parser.utils;

import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.utils.Pair;

/**
 * Utils class to distinguish between the different java types: primitives,
 * references...
 */
public class TypeResolver {

	/**
	 * Get the fully qualified name of a unknown type
	 *
	 * @param type object to analyze
	 * @return fully qualified name
	 */
	public static String getFullyQualifiedName(ResolvedType type) {
		if (type.isPrimitive()) {
			return type.asPrimitive().describe();
		} else if (type.isReferenceType()) {
			return type.asReferenceType().getQualifiedName();
		} else if (type.isTypeVariable()) {
			return type.asTypeVariable().qualifiedName();
		}
		return null;
	}

	/**
	 * Get the fully qualified name of a unknown type
	 *
	 * @param type object to analyze
	 * @return fully qualified name
	 */
	public static String getFullyQualifiedName(ResolvedReferenceType type) {
		if (type.isReferenceType() && type.getQualifiedName().equals("java.lang.Class")) {
			for (Pair<ResolvedTypeParameterDeclaration, ResolvedType> pair : type.asReferenceType()
					.getTypeParametersMap()) {
				if (pair.a.getQualifiedName().equals("java.lang.Class.T")) {
					return pair.b.asReferenceType().getQualifiedName();
				}
			}
		}
		return null;
	}
}
