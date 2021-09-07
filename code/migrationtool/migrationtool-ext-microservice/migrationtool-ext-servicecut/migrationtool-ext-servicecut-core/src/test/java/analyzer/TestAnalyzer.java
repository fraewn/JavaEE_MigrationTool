package analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import model.data.UseCase;
import model.erm.Entity;
import model.erm.EntityRelation;
import model.erm.EntityRelationDiagram;
import model.erm.RelationType;
import objects.Helper;
import operations.dto.AstDTO;

@TestInstance(Lifecycle.PER_CLASS)
public class TestAnalyzer {

	private static final String ENTITY1_PATH = "src/test/java/objects/Entity.java";

	private static final String ENTITY2_PATH = "src/test/java/objects/SecondEntity.java";

	private static final String USECASE1_PATH = "src/test/java/objects/Service.java";

	private static final String USECASE2_PATH = "src/test/java/objects/UseCase.java";

	private List<AstDTO> classes;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		this.classes = new ArrayList<>();

		CombinedTypeSolver solver = new CombinedTypeSolver();
		solver.add(new ReflectionTypeSolver());
		solver.add(new JavaParserTypeSolver("src/main/java"));
		solver.add(new JavaParserTypeSolver("src/test/java"));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
		ParserConfiguration conf = new ParserConfiguration();
		conf.setSymbolResolver(symbolSolver);
		StaticJavaParser.setConfiguration(conf);
		this.classes.addAll(Helper.buildAstDTO(StaticJavaParser.parse(new File(ENTITY1_PATH))));
		this.classes.addAll(Helper.buildAstDTO(StaticJavaParser.parse(new File(ENTITY2_PATH))));
		this.classes.addAll(Helper.buildAstDTO(StaticJavaParser.parse(new File(USECASE1_PATH))));
		this.classes.addAll(Helper.buildAstDTO(StaticJavaParser.parse(new File(USECASE2_PATH))));
	}

	@Test
	public void testAnalyzerEntityDef() {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.findAllDefinedEntities(this.classes);
		List<AstDTO> res = analyzer.getEntities();
		assertEquals(2, res.size());
		List<String> entities = List.of("objects.Entity", "objects.SecondEntity");
		assertEquals(true, entities.contains(res.get(0).getFullName()));
		assertEquals(true, entities.contains(res.get(1).getFullName()));
		assertEquals(false, res.get(0).getFullName().contains(res.get(1).getFullName()));
	}

	@Test
	public void testAnalyzerEntityConvertion() {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.findAllDefinedEntities(this.classes);
		analyzer.convertAllDefinedEntitiesToModel();
		ModelRepresentation res = analyzer.getModel();
		EntityRelationDiagram er = res.getEntityDiagram();
		assertEquals(2, er.getEntities().size());
		Map<String, Integer> map = Map.of("objects.Entity", 4, "objects.SecondEntity", 1);
		for (Entity e : er.getEntities()) {
			assertEquals(true, map.containsKey(e.getName()));
			assertEquals(map.get(e.getName()), e.getAttributes().size());
		}
	}

	@Test
	public void testAnalyzerEntityRelation() {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.findAllDefinedEntities(this.classes);
		analyzer.convertAllDefinedEntitiesToModel();
		analyzer.findAllDefinedEntityRelationships();
		ModelRepresentation res = analyzer.getModel();
		EntityRelationDiagram er = res.getEntityDiagram();
		assertEquals(1, er.getRelations().size());
		assertEquals("objects.Entity", er.getRelations().get(0).getOrigin().getName());
		assertEquals("objects.SecondEntity", er.getRelations().get(0).getDestination().getName());
		assertEquals(RelationType.AGGREGATION, er.getRelations().get(0).getType());
	}

	private static Stream<Arguments> provideArgsForAnalyzerEntityRelationPriorization() {
		Entity e1 = new Entity("object.Hello");
		Entity e2 = new Entity("object.World");
		Entity e3 = new Entity("object.Test");
		EntityRelation er1 = new EntityRelation(e1, e2, RelationType.AGGREGATION);
		EntityRelation er2 = new EntityRelation(e1, e2, RelationType.COMPOSITION);
		EntityRelation er3 = new EntityRelation(e1, e3, RelationType.AGGREGATION);
		EntityRelation er4 = new EntityRelation(e1, e2, RelationType.INHERITANCE);
		EntityRelation er5 = new EntityRelation(e3, e2, RelationType.AGGREGATION);
		EntityRelation er6 = new EntityRelation(e2, e1, RelationType.COMPOSITION);
		return Stream.of(
				Arguments.of(new ArrayList<>(List.of(er1, er2, er3)), 2),
				Arguments.of(new ArrayList<>(List.of(er1)), 1),
				Arguments.of(new ArrayList<>(List.of(er1, er2, er3, er4, er5)), 4),
				Arguments.of(new ArrayList<>(List.of(er1, er4, er5)), 3),
				Arguments.of(new ArrayList<>(List.of(er4, er2, er3)), 3),
				Arguments.of(new ArrayList<>(List.of(er1, er3, er5)), 3),
				Arguments.of(new ArrayList<>(List.of(er1, er6)), 2));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForAnalyzerEntityRelationPriorization")
	public void testAnalyzerEntityRelationPriorization(List<EntityRelation> relations, int expected) {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.removeDuplicatesOfRelationsShips(relations);
		assertEquals(expected, relations.size());
	}

	@Test
	public void testAnalyzerUseCaseDef() {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.findAllDefinedUseCases(this.classes);
		Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> res = analyzer.getUseCases();
		assertEquals(1, res.size());
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : res.entrySet()) {
			assertEquals("objects.UseCase", e.getKey().getFullyQualifiedName().get());
			assertEquals(2, e.getValue().size());
		}
	}

	@Test
	public void testAnalyzerUseCaseAnalyzation() {
		AnalyzerImpl analyzer = new AnalyzerImpl();
		analyzer.findAllDefinedEntities(this.classes);
		analyzer.convertAllDefinedEntitiesToModel();
		analyzer.findAllDefinedEntityRelationships();
		analyzer.findAllDefinedUseCases(this.classes);
		analyzer.analyzeImplementationOfUseCases();
		ArchitectureInformation res = analyzer.getModel().getInformation();
		System.out.println(res.getUseCases());
		assertEquals(1, res.getUseCases().size());
		Map<String, Integer> read = Map.of("objects.UseCase.test1", 1, "objects.UseCase.test2", 0);
		Map<String, Integer> write = Map.of("objects.UseCase.test1", 2, "objects.UseCase.test2", 0);
		for (UseCase e : res.getUseCases()) {
			assertEquals(true, read.containsKey(e.getName()));
			assertEquals(read.get(e.getName()), e.getInput().size());
			assertEquals(true, write.containsKey(e.getName()));
			assertEquals(write.get(e.getName()), e.getPersistenceChanges().size());
		}
	}
}
