package service.extension;

import java.io.File;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import model.ModelRepresentation;
import operations.InterpreterService;
import operations.dto.GenericDTO;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import service.ServiceCutterService;
import service.ServiceCutterServiceImpl;
import ui.AdjacencyMatrix;
import ui.Visualizer;
import ui.VisualizerDummy;
import utils.JsonConverter;
import utils.PropertiesLoader;
import visualizer.LocalVisualizer;

public class ServiceCutterInterpreter extends InterpreterService {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(ServiceCutterInterpreter.class);

	@Option(name = "-visualization", usage = "value for enabling the graphical interface")
	private boolean visualization;

	@Option(name = "-modelEditing", usage = "value for enabling the editing step of the model")
	private boolean editModel;

	@Option(name = "-debug", usage = "value for enabling debug mode")
	private boolean debug;

	@Option(name = "-settingsFile", usage = "path to properties file with defined priorities/settings")
	private String pathToPropertiesFile;

	private PropertiesLoader loader;

	private File file;

	private ModelRepresentation rep;

	private GraphProcessingSteps currentStep;

	private ServiceCutterService imp;

	private Visualizer visual;

	@Override
	public void setDTO(GenericDTO<?> dto) {
		if ((this.pathToPropertiesFile == null) && !this.visualization) {
			throw new MigrationToolInitException("Priorties file needed");
		}
		this.file = new File((String) dto.getObject());
		initialize();
	}

	private void initialize() {
		this.imp = new ServiceCutterServiceImpl();
		this.visual = this.visualization ? new LocalVisualizer() : new VisualizerDummy();
		this.currentStep = GraphProcessingSteps.EDIT_MODEL;
		this.rep = JsonConverter.readJsonFromFile(this.file, ModelRepresentation.class);
		if (!this.visualization) {
			this.loader = new PropertiesLoader(this.pathToPropertiesFile);
			this.loader.loadProps();
		}
	}

	@Override
	public GenericDTO<?> buildDTO() {
		return null;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			throw new MigrationToolRuntimeException(e.getMessage(), e);
		}
	}

	private void execute() {
		LOG.info("Debug is " + (this.debug ? "active, show each step" : "deactived, only necessary steps are shown"));
		do {
			LOG.info("Current Process step: " + this.currentStep.toString());
			LOG.info("Current Process step has substeps? " + this.currentStep.hasSubProcess());
			switch (this.currentStep) {
			case EDIT_MODEL:
				executeEditModel();
				break;
			case NODE_CREATION:
				executeNodeCreation();
				break;
			case EDGE_CREATION:
				executeEdgeCreation();
				break;
			case CALC_WEIGHT:
				executeWeightCalculation();
				break;
			case SOLVE_CLUSTER:
				break;
			default:
				break;
			}
			LOG.info("Finished Process step: " + this.currentStep.toString());
			this.currentStep = this.currentStep.nextStep();
		} while (!this.currentStep.isProcessDone());
	}

	private void executeEditModel() {
		LOG.info("Argument modelEditing is " + (this.editModel ? "true, start modelEditiing" : "false, skip step"));
		if (this.editModel) {
			LOG.info("Start visualization of " + this.file.getName());
			if (!this.visualization) {
				LOG.warn("No Visualization active, skip step");
			}
			this.visual.visualizeModel(JsonConverter.toJsonString(this.rep));
			this.visual.setProgress(this.currentStep.name(), this.currentStep.getProcentOfProgress());
			this.visual.awaitApproval(this.currentStep);
			// Reload
			this.rep = JsonConverter.readJsonFromFile(this.file, ModelRepresentation.class);
		}
	}

	private void executeNodeCreation() {
		LOG.info("Import Architecture from file: " + this.file.getName());
		this.imp.importArchitecture(this.rep);
		LOG.info("Import Architecture successfull");
		if (this.debug) {
			this.visual.visualizeGraph(this.imp.getCurrentGraphState());
			String s = this.currentStep.name();
			this.visual.setProgress(s, this.currentStep.getProcentOfProgress());
			this.visual.awaitApproval(this.currentStep);
		}
	}

	private void executeEdgeCreation() {
		// has substeps
		ProcessAutomate<?> substep = this.currentStep.startState();
		do {
			this.imp.process(this.currentStep, substep);
			if (this.debug) {
				AdjacencyMatrix matrix = this.imp.getCurrentGraphState();
				this.visual.visualizeGraph(matrix);
				String s = this.currentStep.name() + "/" + substep.name();
				this.visual.setProgress(s, this.currentStep.getProcentOfProgress(substep.ordinal()));
				this.visual.awaitApproval(this.currentStep);
			}
			substep = substep.nextStep();
		} while (!this.currentStep.finishedState().equals(substep));
	}

	private void executeWeightCalculation() {
		LOG.info("Calculate weights of each edge");
		this.imp.process(this.currentStep, null);
		LOG.info("Calculation successfull");
		AdjacencyMatrix matrix = this.imp.getCurrentGraphState();
		this.visual.visualizeGraph(matrix);
		this.visual.setProgress(this.currentStep.name(), this.currentStep.getProcentOfProgress());
		this.visual.awaitApproval(this.currentStep);
	}
}
