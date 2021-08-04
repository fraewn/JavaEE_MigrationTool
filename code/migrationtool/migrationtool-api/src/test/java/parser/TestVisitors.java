package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import parser.enums.ArgumentPosition;
import parser.enums.DefinitionTypes;
import parser.enums.ModifierTypes;
import parser.enums.TargetTypes;
import parser.visitors.AccessDeclarationVisitor;
import parser.visitors.AnnotationVisitor;
import parser.visitors.AssignVisitor;
import parser.visitors.ComplexityVisitor;
import parser.visitors.InheritenceVisitor;
import parser.visitors.LinesOfCodeVisitor;
import parser.visitors.MethodArgumentsVisitor;
import parser.visitors.MethodReturnValueVisitor;
import parser.visitors.TautologyVisitor;
import parser.visitors.TypeFieldVisitor;
import parser.visitors.UsedMethodVisitor;
import parser.visitors.UsedTypeVisitor;

@TestInstance(Lifecycle.PER_CLASS)
public class TestVisitors {
	private static final String FILE_PATH = "src/test/java/command/extension/TestCommand.java";

	private CompilationUnit cu;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		CombinedTypeSolver solver = new CombinedTypeSolver();
		solver.add(new ReflectionTypeSolver());
		solver.add(new JavaParserTypeSolver("src/main/java"));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
		ParserConfiguration conf = new ParserConfiguration();
		conf.setSymbolResolver(symbolSolver);
		StaticJavaParser.setConfiguration(conf);
		this.cu = StaticJavaParser.parse(new File(FILE_PATH));
	}

	private static Stream<Arguments> provideArgsForAccessDeclarationVisitor() {
		return Stream.of(
				Arguments.of(ModifierTypes.PUBLIC, true),
				Arguments.of(ModifierTypes.PRIVATE, false),
				Arguments.of(ModifierTypes.PROTECTED, false),
				Arguments.of(ModifierTypes.STATIC, false),
				Arguments.of(ModifierTypes.FINAL, false));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForAccessDeclarationVisitor")
	public void testAccessDeclarationVisitor(ModifierTypes type, boolean expected) {
		AccessDeclarationVisitor visitorPublic = new AccessDeclarationVisitor(type);
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			assertEquals(expected, res);
		});
	}

	private static Stream<Arguments> provideArgsForAnnotationVisitor() {
		return Stream.of(
				Arguments.of(TargetTypes.TYPE, "javax.annotation.Nonnull", true),
				Arguments.of(TargetTypes.TYPE, "javax.annotation.Nullable", false),
				Arguments.of(TargetTypes.METHOD, "javax.annotation.Nonnull", false),
				Arguments.of(TargetTypes.METHOD, "javax.annotation.Nullable", true));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForAnnotationVisitor")
	public void testAnnotationVisitor(TargetTypes type, String searchedAnnotation, boolean expected) {
		AnnotationVisitor visitorPublic = new AnnotationVisitor(searchedAnnotation, type);
		this.cu.findAll(ClassOrInterfaceDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			assertEquals(expected, res);
		});
	}

	private static Stream<Arguments> provideArgsForAssignVisitor() {
		return Stream.of(
				Arguments.of("java.lang.Object", true, true),
				Arguments.of("java.lang.Math", true, false));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForAssignVisitor")
	public void testAssignVisitor(String searchedAnnotation, boolean creation, boolean expected) {
		AssignVisitor visitorPublic = new AssignVisitor(searchedAnnotation, creation);
		this.cu.findAll(ClassOrInterfaceDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			assertEquals(expected, res);
		});
	}

	private static Stream<Arguments> provideArgsForInheritenceVisitor() {
		return Stream.of(
				Arguments.of("operations.CommandExtension", true),
				Arguments.of("java.lang.Math", false));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForInheritenceVisitor")
	public void testInheritenceVisitor(String searchedAnnotation, boolean expected) {
		InheritenceVisitor visitorPublic = new InheritenceVisitor(searchedAnnotation);
		this.cu.findAll(ClassOrInterfaceDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			assertEquals(expected, res);
		});
	}

	private static Stream<Arguments> provideArgsForMethodArgumentsVisitor() {
		return Stream.of(
				Arguments.of("java.lang.String", ArgumentPosition.DECLARATION, 3),
				Arguments.of("java.lang.Math", ArgumentPosition.DECLARATION, 0),
				Arguments.of("java.lang.Object", ArgumentPosition.CALL_EXPRESSION, 1));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForMethodArgumentsVisitor")
	public void testMethodArgumentsVisitor(String searchedEntity, ArgumentPosition pos, int expected) {
		MethodArgumentsVisitor visitorPublic = new MethodArgumentsVisitor(searchedEntity, pos);
		AtomicInteger counter = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			if (res) {
				counter.incrementAndGet();
			}
		});
		assertEquals(expected, counter.get());
	}

	private static Stream<Arguments> provideArgsForMethodReturnValueVisitor() {
		return Stream.of(
				Arguments.of("java.lang.String", 4),
				Arguments.of("java.lang.Math", 0),
				Arguments.of("java.lang.Object", 0));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForMethodReturnValueVisitor")
	public void testMethodReturnValueVisitor(String searchedEntity, int expected) {
		MethodReturnValueVisitor visitorPublic = new MethodReturnValueVisitor(searchedEntity);
		AtomicInteger counter = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			if (res) {
				counter.incrementAndGet();
			}
		});
		assertEquals(expected, counter.get());
	}

	private static Stream<Arguments> provideArgsForTypeFieldVisitor() {
		return Stream.of(
				Arguments.of(DefinitionTypes.ALL, "java.lang.String", false, 1),
				Arguments.of(DefinitionTypes.ALL, "java.lang.String", true, 2),
				Arguments.of(DefinitionTypes.ALL, "java.lang.Math", false, 0),
				Arguments.of(DefinitionTypes.ALL, "java.util.List", false, 1));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForTypeFieldVisitor")
	public void testTypeFieldVisitor(DefinitionTypes types, String searchedEntity, boolean typeParameter,
			int expected) {
		TypeFieldVisitor visitorPublic = new TypeFieldVisitor(types, searchedEntity, typeParameter);
		AtomicInteger counter = new AtomicInteger();
		this.cu.findAll(FieldDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			if (res) {
				counter.incrementAndGet();
			}
		});
		assertEquals(expected, counter.get());
	}

	private static Stream<Arguments> provideArgsForUsedMethodVisitor() {
		return Stream.of(
				Arguments.of("java.lang.String", "toString", 0),
				Arguments.of("java.lang.Object", "toString", 1));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForUsedMethodVisitor")
	public void testUsedMethodVisitor(String searchedEntity, String searchMethod, int expected) {
		UsedMethodVisitor visitorPublic = new UsedMethodVisitor(searchedEntity, searchMethod);
		AtomicInteger counter = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			if (res) {
				counter.incrementAndGet();
			}
		});
		assertEquals(expected, counter.get());
	}

	private static Stream<Arguments> provideArgsForUsedTypeVisitor() {
		return Stream.of(
				Arguments.of("java.lang.String", "toString", 0),
				Arguments.of("java.lang.Object", "toString", 1));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForUsedTypeVisitor")
	public void testUsedTypeVisitor(String searchedEntity, String searchMethod, int expected) {
		UsedTypeVisitor visitorPublic = new UsedTypeVisitor(searchedEntity, searchMethod);
		AtomicInteger counter = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			if (res) {
				counter.incrementAndGet();
			}
		});
		assertEquals(expected, counter.get());
	}

	private static Stream<Arguments> provideArgsForComplexityVisitor() {
		return Stream.of(
				Arguments.of(new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 1 }, true));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForComplexityVisitor")
	public void testComplexityVisitor(int[] expected, boolean result) {
		ComplexityVisitor visitorPublic = new ComplexityVisitor();
		AtomicInteger z = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			AtomicInteger counter = new AtomicInteger();
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, counter)).orElse(false);
			assertEquals(result, res);
			assertEquals(expected[z.get()], counter.get());
			z.incrementAndGet();
		});

	}

	private static Stream<Arguments> provideArgsForLinesOfCodeVisitor() {
		return Stream.of(
				Arguments.of(new int[] { 1, 1, 1, 1, 1, 1, 1, 0, 10, 0, 0 }, true));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForLinesOfCodeVisitor")
	public void testLinesOfCodeVisitor(int[] expected, boolean result) {
		LinesOfCodeVisitor visitorPublic = new LinesOfCodeVisitor();
		AtomicInteger z = new AtomicInteger();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			AtomicInteger counter = new AtomicInteger();
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, counter)).orElse(false);
			assertEquals(result, res);
			assertEquals(expected[z.get()], counter.get());
			z.incrementAndGet();
		});
	}

	@Test
	public void testTautologyVisitor() {
		TautologyVisitor visitorPublic = new TautologyVisitor();
		this.cu.findAll(MethodDeclaration.class).forEach(x -> {
			Boolean res = Optional.ofNullable(x.accept(visitorPublic, null)).orElse(false);
			assertEquals(true, res);
		});
	}
}
