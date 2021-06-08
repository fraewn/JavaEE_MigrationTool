package rules.engine;

import java.util.Map;

import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.visitor.GenericVisitor;

import data.ArgumentPosition;
import data.DefinitionTypes;
import data.ModifierTypes;
import data.TargetTypes;
import exceptions.MigrationToolInitException;
import parser.visitors.AccessDeclarationVisitor;
import parser.visitors.AnnotationVisitor;
import parser.visitors.AssignVisitor;
import parser.visitors.InheritenceVisitor;
import parser.visitors.MethodArgumentsVisitor;
import parser.visitors.MethodReturnValueVisitor;
import parser.visitors.TypeFieldVisitor;
import parser.visitors.UsedMethodVisitor;
import parser.visitors.UsedTypeVisitor;
import rules.RuleDefinition;
import rules.definitions.TautologyVisitor;

public class VisitorFactory {

	private VisitorFactory() {

	}

	public static GenericVisitor<Boolean, ?> buildVisitor(RuleDefinition definition, Map<String, String> args) {
		switch (definition) {
		case TAUTOLOGY:
			return createTautologyVisitor();
		case ANNOTATION:
			return createAnnotationVisitor(args);
		case MODIFIER:
			return createModifierVisitor(args);
		case ASSIGN:
			return createAssignVisitor(args);
		case TYPED_CALL:
			return createTypeCallVisitor(args);
		case METHOD:
			return createMethodVisitor(args);
		case METHOD_CALL:
			return createMethodCallVisitor(args);
		case METHOD_RETURN:
			return createMethodReturnVisitor(args);
		case METHOD_ARG:
			return createMethodArgVisitor(args);
		case METHOD_CALL_ARG:
			return createMethodCallArgVisitor(args);
		case AGGREGATION:
			return createAggregationVisitor(args);
		case COMPOSITION:
			return createCompositionVisitor(args);
		case INHERITENCE:
			return createInheritenceVisitor(args);
		default:
			throw new MigrationToolInitException("Unknown Definition " + definition);
		}
	}

	private static TautologyVisitor createTautologyVisitor() {
		return new TautologyVisitor();
	}

	private static AnnotationVisitor createAnnotationVisitor(Map<String, String> args) {
		String searchedAnnotation = args.get("name");
		TargetTypes type = TargetTypes.valueOf(args.get("type"));
		return new AnnotationVisitor(searchedAnnotation, type);
	}

	private static AccessDeclarationVisitor createModifierVisitor(Map<String, String> args) {
		ModifierTypes type = ModifierTypes.valueOf(args.get("type"));
		return new AccessDeclarationVisitor(type);
	}

	private static AssignVisitor createAssignVisitor(Map<String, String> args) {
		String searched = args.get("name");
		return new AssignVisitor(searched, false, Operator.ASSIGN);
	}

	private static UsedTypeVisitor createTypeCallVisitor(Map<String, String> args) {
		String searchedType = args.get("name");
		String[] searchedMethods = args.get("methods").split(",");
		return new UsedTypeVisitor(searchedType, searchedMethods);
	}

	private static UsedMethodVisitor createMethodVisitor(Map<String, String> args) {
		String searchedMethod = args.get("name");
		String searchedClass = args.get("class");
		return new UsedMethodVisitor(searchedClass, searchedMethod);
	}

	private static UsedMethodVisitor createMethodCallVisitor(Map<String, String> args) {
		String searchedMethod = args.get("name");
		String searchedClass = args.get("class");
		String[] searchedArg = args.get("arg").split(",");
		return new UsedMethodVisitor(searchedClass, searchedMethod, true, searchedArg);
	}

	private static MethodReturnValueVisitor createMethodReturnVisitor(Map<String, String> args) {
		String searchedReturn = args.get("name");
		return new MethodReturnValueVisitor(searchedReturn);
	}

	private static MethodArgumentsVisitor createMethodArgVisitor(Map<String, String> args) {
		String searchedArg = args.get("name");
		return new MethodArgumentsVisitor(searchedArg, ArgumentPosition.DECLARATION);
	}

	private static MethodArgumentsVisitor createMethodCallArgVisitor(Map<String, String> args) {
		String searchedArg = args.get("name");
		return new MethodArgumentsVisitor(searchedArg, ArgumentPosition.CALL_EXPRESSION);
	}

	private static TypeFieldVisitor createAggregationVisitor(Map<String, String> args) {
		String searched = args.get("name");
		DefinitionTypes type = DefinitionTypes.valueOf(args.get("type"));
		return new TypeFieldVisitor(type, searched, true);
	}

	private static AssignVisitor createCompositionVisitor(Map<String, String> args) {
		String searched = args.get("name");
		return new AssignVisitor(searched, true);
	}

	private static InheritenceVisitor createInheritenceVisitor(Map<String, String> args) {
		String searched = args.get("name");
		return new InheritenceVisitor(searched);
	}
}
