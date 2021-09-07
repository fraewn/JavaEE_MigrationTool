package rules.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.visitor.GenericVisitor;

import parser.enums.ArgumentPosition;
import parser.enums.DefinitionTypes;
import parser.enums.ModifierTypes;
import parser.enums.TargetTypes;
import parser.visitors.AccessDeclarationVisitor;
import parser.visitors.AnnotationGroupingVisitor;
import parser.visitors.AnnotationVisitor;
import parser.visitors.AssignVisitor;
import parser.visitors.ComplexityVisitor;
import parser.visitors.InheritenceVisitor;
import parser.visitors.LinesOfCodeVisitor;
import parser.visitors.MethodArgumentsVisitor;
import parser.visitors.MethodReturnValueVisitor;
import parser.visitors.OccurrenceVisitor;
import parser.visitors.TautologyVisitor;
import parser.visitors.TypeFieldVisitor;
import parser.visitors.UsedMethodVisitor;
import parser.visitors.UsedTypeVisitor;

/**
 * All possible rules which can be set in the corresponding configuration file
 */
public enum RuleDefinition {
	/**
	 * This rule is always true
	 */
	TAUTOLOGY() {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			return new TautologyVisitor();
		}
	},
	/**
	 * Search for a specific annotation
	 */
	ANNOTATION("name", "type") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedAnnotation = args.get("name");
			TargetTypes type = TargetTypes.valueOf(args.get("type"));
			return new AnnotationVisitor(searchedAnnotation, type);
		}
	},
	/**
	 * Search for a specific inheritence relationship
	 */
	INHERITENCE("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searched = args.get("name");
			return new InheritenceVisitor(searched);
		}
	},
	/**
	 * Search for a specific composition relationship
	 */
	COMPOSITION("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searched = args.get("name");
			return new AssignVisitor(searched, true);
		}
	},
	/**
	 * Search for a specific aggregation relationship
	 */
	AGGREGATION("name", "type") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searched = args.get("name");
			DefinitionTypes type = DefinitionTypes.valueOf(args.get("type"));
			return new TypeFieldVisitor(type, searched, true);
		}
	},
	/**
	 * Search for a specific modifier type {@link ModifierTypes}
	 */
	MODIFIER("type") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			ModifierTypes type = ModifierTypes.valueOf(args.get("type"));
			return new AccessDeclarationVisitor(type);
		}
	},
	/**
	 * Search for a specific assignment operation
	 */
	ASSIGN("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searched = args.get("name");
			return new AssignVisitor(searched, false, Operator.ASSIGN);
		}
	},
	/**
	 * Search for a argument in the defined method list of a specific class
	 */
	TYPED_CALL("name", "methods") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedType = args.get("name");
			String[] searchedMethods = args.get("methods").split(",");
			return new UsedTypeVisitor(searchedType, searchedMethods);
		}
	},
	/**
	 * Search for a specific method
	 */
	METHOD("class", "name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedMethod = args.get("name");
			String searchedClass = args.get("class");
			return new UsedMethodVisitor(searchedClass, searchedMethod);
		}
	},
	/**
	 * Search for a specific method call
	 */
	METHOD_CALL("class", "name", "arg") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedMethod = args.get("name");
			String searchedClass = args.get("class");
			String[] searchedArg = args.get("arg").split(",");
			return new UsedMethodVisitor(searchedClass, searchedMethod, true, searchedArg);
		}
	},
	/**
	 * Search for a specific method return type
	 */
	METHOD_RETURN("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedReturn = args.get("name");
			return new MethodReturnValueVisitor(searchedReturn);
		}
	},
	/**
	 * Search for a specific method argument in the method declaration
	 */
	METHOD_ARG("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedArg = args.get("name");
			return new MethodArgumentsVisitor(searchedArg, ArgumentPosition.DECLARATION);
		}
	},
	/**
	 * Search for a specific method call argument
	 */
	METHOD_CALL_ARG("name") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedArg = args.get("name");
			return new MethodArgumentsVisitor(searchedArg, ArgumentPosition.CALL_EXPRESSION);
		}
	},
	/**
	 * Recommendation Visitor; Counts the complexity of a method
	 */
	COMPLEXITY() {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			return new ComplexityVisitor();
		}
	},
	/**
	 * Recommendation Visitor; Counts the lines of a method
	 */
	LINES_OF_CODE() {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			return new LinesOfCodeVisitor();
		}
	},
	/**
	 * Recommendation Visitor; Counts included instances of a method
	 */
	OCCURENCE() {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			return new OccurrenceVisitor();
		}
	},
	/**
	 * Recommendation Visitor; Groups by single member annotation
	 */
	ANNOTATION_GROUPING("name", "type") {
		@Override
		public GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args) {
			String searchedAnnotation = args.get("name");
			TargetTypes type = TargetTypes.valueOf(args.get("type"));
			return new AnnotationGroupingVisitor(searchedAnnotation, type);
		}
	};

	/** list of required parameters */
	private List<String> requiredArgs;

	RuleDefinition(String... requiredArgs) {
		this.requiredArgs = Arrays.asList(requiredArgs);
	}

	/**
	 * Constructs a visitor from the given parameters
	 *
	 * @param args initialization parameters
	 * @return Visitor
	 */
	public abstract GenericVisitor<Boolean, ?> buildVisitor(Map<String, String> args);

	/**
	 * @return the requiredArgs
	 */
	public List<String> getRequiredArgs() {
		return this.requiredArgs;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
