package rules;

import java.util.Map;

import com.github.javaparser.ast.visitor.GenericVisitor;

import data.TargetTypes;
import exceptions.MigrationToolInitException;
import parser.visitors.AnnotationVisitor;

public class VisitorFactory {

	private VisitorFactory() {

	}

	public static GenericVisitor<Boolean, ?> buildVisitor(RuleDefinition definition) {
		switch (definition) {
		case ANNOTATION:
			return createAnnotationVisitor(definition.getArgs());
		default:
			throw new MigrationToolInitException("Unknown Definition " + definition);
		}
	}

	private static AnnotationVisitor createAnnotationVisitor(Map<String, String> args) {
		String searchedAnnotation = args.get("name");
		TargetTypes type = TargetTypes.valueOf(args.get("type"));
		return new AnnotationVisitor(searchedAnnotation, type);
	}
}
