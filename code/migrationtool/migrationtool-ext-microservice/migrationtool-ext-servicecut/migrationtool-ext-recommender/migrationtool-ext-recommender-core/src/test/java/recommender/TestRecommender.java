package recommender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

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

import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import model.data.UseCase;
import model.erm.Entity;
import objects.Helper;
import operations.dto.AstDTO;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import recommender.service.RecommenderService;

@TestInstance(Lifecycle.PER_CLASS)
public class TestRecommender {

	private static final String ENTITY_PATH = "src/test/java/objects/Entity.java";

	private static final String USECASE_PATH = "src/test/java/objects/UseCase.java";

	private RecommenderService recommender;

	private ModelRepresentation model;

	private Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases;

	private List<AstDTO> entites;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		this.model = new ModelRepresentation();

		CombinedTypeSolver solver = new CombinedTypeSolver();
		solver.add(new ReflectionTypeSolver());
		solver.add(new JavaParserTypeSolver("src/main/java"));
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

	@BeforeEach
	public void initRecommender() {
		this.recommender = new Recommender();
		this.recommender.importArchitecture(this.model, this.entites, this.useCases);
	}

	@Test
	public void testRecommenderGetInformation() {
		List<Integer> list = this.recommender.getInformation(RecommenderProcessingSteps.LATENCY);
		assertEquals(1, list.size());
		List<Integer> list2 = this.recommender.getInformation(RecommenderProcessingSteps.FINISHED);
		assertEquals(0, list2.size());
	}

	@Test
	public void testRecommenderProcess() {
		Map<String, Recommendation> list = this.recommender.process(RecommenderProcessingSteps.LATENCY);
		assertEquals(2, list.size());
		List<Recommendation> filtered = list.values().stream().filter(Recommendation::isIncluded)
				.collect(Collectors.toList());
		assertEquals(1, filtered.size());
		int linesOfCode = 15;
		int complexity = 4;
		assertEquals(linesOfCode + complexity, filtered.get(0).getMetricValue());
		assertEquals("objects.UseCase.test1", filtered.get(0).getRelatedGroup());
	}

	@Test
	public void testRecommenderConvert() {
		Map<String, Recommendation> list = this.recommender.process(RecommenderProcessingSteps.LATENCY);
		ArchitectureInformation ai = this.recommender.convertRecommendations(new ArrayList<>(list.values()),
				RecommenderProcessingSteps.LATENCY);
		assertNotNull(ai);

	}
}
