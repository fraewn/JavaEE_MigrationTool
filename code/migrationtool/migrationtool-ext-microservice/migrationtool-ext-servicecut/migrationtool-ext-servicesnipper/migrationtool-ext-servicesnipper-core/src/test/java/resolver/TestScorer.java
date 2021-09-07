package resolver;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import model.ModelRepresentation;
import service.ServiceSnipperService;
import service.ServiceSnipperServiceImpl;
import utils.JsonConverter;

@TestInstance(Lifecycle.PER_CLASS)
public class TestScorer {

	private ServiceSnipperService service;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		this.service = new ServiceSnipperServiceImpl();
		this.service
				.importArchitecture(JsonConverter.readJsonFromFile(new File("model.json"), ModelRepresentation.class));
	}

//	@Test
//	public void testAnalyzerEntityDef() {
//		this.service.process(GraphProcessingSteps.EDGE_CREATION, GraphCreationSteps.SAME_CONTEXT);
//		CriteriaScorer scorer = CriteriaScorerFactory.getScorer(CSCohesiveGroup.class);
//		scorer.getScores(null, null);
//	}
}
