package parser.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;

import parser.enums.DefinitionTypes;
import parser.utils.TypeResolver;

/**
 * Filter the AST-tree for a specific type
 */
public class TypeFieldVisitor extends GenericVisitorAdapter<Boolean, FieldDeclaration> {

	/** searched java type */
	private DefinitionTypes type;
	/** searched definition */
	private String searchedDefinition;
	/** inspect type parameter e.g. List<XXX> */
	private boolean typeParameterInspection;

	public TypeFieldVisitor(DefinitionTypes type) {
		this(type, null, false);
	}

	public TypeFieldVisitor(DefinitionTypes type, String searchedDefinition) {
		this(type, searchedDefinition, false);
	}

	public TypeFieldVisitor(DefinitionTypes type, String searchedDefinition, boolean typeParameterInspection) {
		this.type = type;
		this.searchedDefinition = searchedDefinition;
		this.typeParameterInspection = typeParameterInspection;
	}

	@Override
	public Boolean visit(FieldDeclaration n, FieldDeclaration container) {
		List<ResolvedType> types = new ArrayList<>();
		ResolvedType type = n.resolve().getType();
		types.add(type);
		if (this.typeParameterInspection) {
			if (n.getElementType().isClassOrInterfaceType()) {
				NodeList<Type> tmp = n.getElementType().asClassOrInterfaceType().getTypeArguments()
						.orElse(new NodeList<Type>());
				for (Type t : tmp) {
					types.add(t.resolve());
				}
			}
		}
		for (ResolvedType resolvedType : types) {
			String s = TypeResolver.getFullyQualifiedName(resolvedType);
			boolean match = this.searchedDefinition != null ? s.equals(this.searchedDefinition) : true;
			if (this.type.equals(DefinitionTypes.ALL) && match) {
				return Boolean.TRUE;
			}
			if (this.type.equals(DefinitionTypes.PRIMITIVE) && resolvedType.isPrimitive() && match) {
				return Boolean.TRUE;
			}
			if (this.type.equals(DefinitionTypes.CLASS) && resolvedType.isReference()) {
				if (checkReference(resolvedType) && match) {
					return Boolean.TRUE;
				}
			} else if (this.type.equals(DefinitionTypes.JAVA_CLASSES) && resolvedType.isReference()) {
				if (checkReference(resolvedType) && s.startsWith("java") && match) {
					return Boolean.TRUE;
				}
			} else if (this.type.equals(DefinitionTypes.EXTERN) && resolvedType.isReference()) {
				if (checkReference(resolvedType) && !s.startsWith("java") && match) {
					return Boolean.TRUE;
				}
			}
		}
		return super.visit(n, container);
	}

	private boolean checkReference(ResolvedType type) {
		if (type.isArray()) {
			ResolvedType comp = type.asArrayType().getComponentType();
			if (comp.isReference()) {
				return checkReference(comp);
			}
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
