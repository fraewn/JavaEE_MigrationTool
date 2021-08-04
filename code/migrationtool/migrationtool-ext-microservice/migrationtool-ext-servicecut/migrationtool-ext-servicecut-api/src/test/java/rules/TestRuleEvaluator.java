package rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
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
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import exceptions.MigrationToolInitException;
import model.ModelRepresentation;
import model.data.UseCase;
import model.erm.Entity;
import objects.Helper;
import operations.dto.AstDTO;
import rules.engine.RuleEvaluator;
import rules.engine.WildCards;

@TestInstance(Lifecycle.PER_CLASS)
public class TestRuleEvaluator {

	private static final String ENTITY_PATH = "src/test/java/objects/Entity.java";

	private static final String USECASE_PATH = "src/test/java/objects/UseCase.java";

	private ModelRepresentation model;

	private Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases;

	private List<AstDTO> entites;

	private RuleEvaluator ruleEvaluator;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		this.model = new ModelRepresentation();

		CombinedTypeSolver solver = new CombinedTypeSolver();
		solver.add(new ReflectionTypeSolver());
		solver.add(new JavaParserTypeSolver("src/main/java"));
		solver.add(new JavaParserTypeSolver("src/test/java"));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
		ParserConfiguration conf = new ParserConfiguration();
		conf.setSymbolResolver(symbolSolver);
		StaticJavaParser.setConfiguration(conf);
		CompilationUnit entity = StaticJavaParser.parse(new File(ENTITY_PATH));
		this.entites = Helper.buildAstDTO(entity);
		for (AstDTO astDTO : this.entites) {
			Entity e = new Entity();
			e.setName(astDTO.getFullName());
			for (FieldDeclaration field : astDTO.getFields()) {
				for (VariableDeclarator variable : field.getVariables()) {
					e.getAttributes().add(variable.getNameAsString());
				}
			}
			this.model.getEntityDiagram().getEntities().add(e);
		}
		CompilationUnit useCase = StaticJavaParser.parse(new File(USECASE_PATH));
		this.useCases = new HashMap<>();
		List<AstDTO> temp = Helper.buildAstDTO(useCase);
		for (AstDTO astDTO : temp) {
			this.useCases.put(astDTO.getJavaClass(), new ArrayList<>());
			this.useCases.get(astDTO.getJavaClass()).addAll(astDTO.getMethods());
			for (MethodDeclaration method : astDTO.getMethods()) {
				UseCase use = new UseCase();
				use.setName(astDTO.getFullName() + "." + method.getNameAsString());
				use.setPersistenceChangesAsString("objects.Entity.id");
				this.model.getInformation().getUseCases().add(use);
			}

		}
	}

	@Test
	public void testRuleEvaluatorWorkflow() {
		this.ruleEvaluator = new RuleEvaluator();
		this.ruleEvaluator.newStatement("tautology[]");
		AtomicBoolean success = new AtomicBoolean();
		this.ruleEvaluator.evaluate(this.entites.get(0).getJavaClass(), x -> {
			success.set(true);
		});
		assertEquals(true, success.get());
		success.set(false);
		this.ruleEvaluator.evaluate(this.entites.get(0).getJavaClass(), x -> {
			success.set(true);
		});
		assertEquals(true, success.get());
		this.ruleEvaluator.newStatement("NOT tautology[]");
		success.set(false);
		this.ruleEvaluator.evaluate(this.entites.get(0).getJavaClass(), x -> {
			success.set(true);
		});
		assertEquals(false, success.get());

	}

	private static Stream<Arguments> provideArgsForRuleEvaluatorFormat() {
		return Stream.of(
				Arguments.of("unknown", MigrationToolInitException.class),
				Arguments.of("tautology", MigrationToolInitException.class),
				Arguments.of("tautology[toMuch]", MigrationToolInitException.class),
				Arguments.of("NOT modifier[unknown=STATIC]", MigrationToolInitException.class),
				Arguments.of("NOT modifier[name=2, type=STATIC]", MigrationToolInitException.class),
				Arguments.of("AND", MigrationToolInitException.class));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForRuleEvaluatorFormat")
	public void testRuleEvaluatorFormat(String statement, Class<? extends Throwable> throwable) {
		this.ruleEvaluator = new RuleEvaluator();
		assertThrows(throwable, () -> this.ruleEvaluator.newStatement(statement));
	}

	private static Stream<Arguments> provideArgsForRuleEvaluatorAgainstEntity() {
		return Stream.of(
				Arguments.of("tautology[]", new boolean[] { true }),
				Arguments.of("TAUTOLOGY[]", new boolean[] { true }),
				Arguments.of("tautology[] AND tautology[]", new boolean[] { true }),
				Arguments.of("tautology[] AND NOT tautology[]", new boolean[] { false }),
				Arguments.of("tautology[] OR NOT tautology[]", new boolean[] { true }),
				Arguments.of("NOT modifier[type=STATIC]", new boolean[] { true }),
				Arguments.of("modifier[type=FINAL]", new boolean[] { false }));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForRuleEvaluatorAgainstEntity")
	public void testRuleEvaluatorAgainstEntity(String statement, boolean[] expected) {
		this.ruleEvaluator = new RuleEvaluator();
		this.ruleEvaluator.newStatement(statement);
		AtomicBoolean success = new AtomicBoolean();
		int z = 0;
		for (AstDTO astDTO : this.entites) {
			this.ruleEvaluator.evaluate(astDTO.getJavaClass(), x -> {
				success.set(true);
			});
			assertEquals(expected[z], success.get());
			z++;
			success.set(false);
		}
	}

	private static Stream<Arguments> provideArgsForRuleEvaluatorAgainstUseCase() {
		return Stream.of(
				Arguments.of("tautology[]", new boolean[] { true, true }),
				Arguments.of("method_return[name=java.lang.String]", new boolean[] { false, true }),
				Arguments.of("assign[name=objects.Entity]", new boolean[] { true, false }),
				Arguments.of("method_arg[name=java.lang.String]", new boolean[] { false, false }),
				Arguments.of("method_call_arg[name=java.lang.String]", new boolean[] { true, true }),
				Arguments.of("method[class=objects.Entity; name=setRequired]", new boolean[] { true, false }));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForRuleEvaluatorAgainstUseCase")
	public void testRuleEvaluatorAgainstUseCase(String statement, boolean[] expected) {
		this.ruleEvaluator = new RuleEvaluator();
		this.ruleEvaluator.newStatement(statement);
		AtomicBoolean success = new AtomicBoolean();
		int z = 0;
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> astDTO : this.useCases.entrySet()) {
			for (MethodDeclaration method : astDTO.getValue()) {
				this.ruleEvaluator.evaluate(method, x -> {
					success.set(true);
				});
				assertEquals(expected[z], success.get());
				z++;
				success.set(false);
			}
		}
	}

	private static Stream<Arguments> provideArgsForRuleEvaluatorRecommendation() {
		return Stream.of(
				Arguments.of("lines_Of_Code[]", new int[] { 15, 5 }),
				Arguments.of("complexity[]", new int[] { 4, 1 }));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForRuleEvaluatorRecommendation")
	public void testRuleEvaluatorRecommendation(String statement, int[] expected) {
		this.ruleEvaluator = new RuleEvaluator();
		this.ruleEvaluator.newStatement(statement);
		AtomicBoolean success = new AtomicBoolean();
		AtomicInteger count = new AtomicInteger();
		int z = 0;
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> astDTO : this.useCases.entrySet()) {
			for (MethodDeclaration method : astDTO.getValue()) {
				this.ruleEvaluator.recommand(method, x -> {
					success.set(true);
					count.set(x);
				});
				assertEquals(true, success.get());
				assertEquals(expected[z], count.get());
				z++;
				success.set(false);
			}
		}
	}

	@Test
	public void testRuleEvaluatorWildCards() {
		this.ruleEvaluator = new RuleEvaluator();
		this.ruleEvaluator.newStatement("method_call_arg[name=?entity_name?]");
		List<WildCards> wildCards = new ArrayList<>();
		wildCards.add(WildCards.ENTITY_NAME.setValue("java.lang.String"));
		AtomicBoolean success = new AtomicBoolean();
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> astDTO : this.useCases.entrySet()) {
			for (MethodDeclaration method : astDTO.getValue()) {
				this.ruleEvaluator.evaluate(wildCards, method, x -> {
					success.set(true);
				});
				assertEquals(true, success.get());
				success.set(false);
			}
		}
	}
}
