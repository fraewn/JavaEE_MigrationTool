package parser.utils;

import com.github.javaparser.resolution.types.ResolvedType;

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
		}
		return null;
	}
}
