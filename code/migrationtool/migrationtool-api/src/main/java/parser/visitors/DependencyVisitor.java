package parser.visitors;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;

import data.TargetTypes;
import parser.utils.TypeResolver;

public class DependencyVisitor extends GenericVisitorAdapter<Boolean, String> {

	private List<String> blackListAnnotations;

	public DependencyVisitor(String... blacklistedAnnotations) {
		this.blackListAnnotations = Arrays.asList(blacklistedAnnotations);
	}

	@Override
	public Boolean visit(FieldDeclaration n, String arg) {
		for (String s : this.blackListAnnotations) {
			AnnotationVisitor av = new AnnotationVisitor(s, TargetTypes.FIELD);
			boolean blacklisted = Optional.ofNullable(n.accept(av, null)).orElse(false);
			if (blacklisted) {
				return super.visit(n, arg);
			}
		}
		for (VariableDeclarator variable : n.getVariables()) {
			ResolvedType type = variable.getType().resolve();
			String qualifiedName = TypeResolver.getFullyQualifiedName(type);
			System.out.println(qualifiedName);
		}
		return super.visit(n, arg);
	}
}
