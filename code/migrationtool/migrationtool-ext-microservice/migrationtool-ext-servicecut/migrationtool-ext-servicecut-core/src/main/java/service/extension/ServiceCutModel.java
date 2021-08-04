package service.extension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

import analyzer.Analyzer;
import exceptions.MigrationToolRuntimeException;
import model.ModelRepresentation;
import operations.ModelService;
import operations.dto.AstDTO;
import recommandation.service.RecommanderVisualizer;
import recommender.Recommender;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import recommender.service.RecommenderService;
import recommender.service.gui.Visualizer;
import recommender.service.gui.VisualizerAdapter;
import utils.JsonConverter;
import utils.StateMachine;

/**
 * Implementation of the process step to create the decomposition model
 */
public class ServiceCutModel extends ModelService<List<AstDTO>, String> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Default file name */
	private static final String DEFAULT_MODEL_FILE_NAME = "servicecut_model";

	@Option(name = "-output", usage = "value for defining the location of the json file")
	private String modelPath;

	@Option(name = "-outputName", usage = "value for defining the name of the json file")
	private String modelName;

	@Option(name = "-visualization", usage = "value for enabling the graphical interface")
	private boolean visualization;

	@Option(name = "-recommendation", usage = "value for enabling the recommender system")
	private boolean recommendation;

	@Option(name = "-skipModel", usage = "value for enabling the model analyzation")
	private boolean skip;
	/** The process state machine */
	private StateMachine<RecommenderProcessingSteps> stateMachine;
	/** Reference to the visualization */
	private Visualizer visual;
	/** Reference to the service interface of the recommendation engine */
	private RecommenderService recommender;
	/** The result file */
	private File resultFile;

	@Override
	public String save(List<AstDTO> input) {
		// init default values
		this.modelName = this.modelName == null ? DEFAULT_MODEL_FILE_NAME : this.modelName;
		this.modelPath = this.modelPath == null ? "" : this.modelPath + "/";
		this.resultFile = new File(this.modelPath + this.modelName + ".json");
		// skip analyzation process
		if (!this.skip) {
			Analyzer analyzer = new Analyzer();
			analyzer.analyze(input);
			ModelRepresentation rep = analyzer.getModel();
			// start recommendation engine
			if (this.recommendation) {
				this.recommender = new Recommender();
				this.visual = this.visualization ? new RecommanderVisualizer(this.recommender)
						: new VisualizerAdapter();
				this.stateMachine = new StateMachine<>(RecommenderProcessingSteps.class);
				execute(analyzer);
			}
			// write result file
			if (this.resultFile.exists()) {
				LOG.warn("Override: " + this.resultFile.getName());
				this.resultFile.delete();
			}
			try {
				this.resultFile.createNewFile();
				JsonConverter.toJsonString(this.resultFile, rep);
			} catch (IOException e) {
				throw new MigrationToolRuntimeException(e.getMessage());
			}
		}
		return this.resultFile == null ? null : this.resultFile.getAbsolutePath();
	}

	private void execute(Analyzer analyzer) {
		ModelRepresentation model = analyzer.getModel();
		RecommenderProcessingSteps currentStep = this.stateMachine.startState();
		this.recommender.importArchitecture(model, analyzer.getEntities(), analyzer.getUseCases());
		do {
			LOG.info("Current Process step: " + currentStep.toString());
			Map<String, Recommendation> result = this.recommender.process(currentStep);
			if (!result.isEmpty()) {
				this.visual.visualizeModel(result, currentStep);
				this.visual.setProgress(currentStep.name(), this.stateMachine.getProcentOfProgress());
				boolean discard = this.visual.awaitApproval(currentStep);
				if (!discard) {
					List<Recommendation> finalRes = this.visual.getAppliedChanges();
					finalRes = finalRes == null ? new ArrayList<>(result.values()) : finalRes;
					currentStep.convertToModel(model, this.recommender.convertRecommendations(finalRes, currentStep));
				}
			}
			LOG.info("Finished Process step: " + currentStep.toString());
			currentStep = this.stateMachine.nextStep();
		} while (!this.stateMachine.isProcessDone());
		LOG.info("Finished Process");
		this.visual.stop();
	}
}
