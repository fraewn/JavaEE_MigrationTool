package parser.utils;

import java.util.List;

import com.github.javaparser.resolution.types.ResolvedType;

import operations.dto.ClassDTO;
import operations.dto.GenericDTO;

public class TypeResolver {

	public static String getFullyQualifiedName(ResolvedType type) {
		if (type.isPrimitive()) {
			return type.asPrimitive().describe();
		} else if (type.isReferenceType()) {
			return type.asReferenceType().getQualifiedName();
		}
		new GenericDTO<List<ClassDTO>>(null);
		return null;
	}
}
