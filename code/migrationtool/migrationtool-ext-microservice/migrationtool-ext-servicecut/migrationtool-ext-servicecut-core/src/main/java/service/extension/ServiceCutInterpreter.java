package service.extension;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

import application.StartUp;
import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.processing.GraphProcessingSteps;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;
import operations.InterpreterService;
import service.ServiceSnipperService;
import service.ServiceSnipperServiceImpl;
import service.gui.ResolverConfiguration;
import service.gui.Visualizer;
import service.gui.VisualizerAdapter;
import servicesnipper.service.ServiceSnipperVisualizer;
import utils.JsonConverter;
import utils.PropertiesLoader;
import utils.StateMachine;
import validation.ModelValidator;

/**
 * Implementation of the process step to create the real cut of the services
 */
public class ServiceCutInterpreter extends InterpreterService<String, Object> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private final Map<GraphProcessingSteps, Runnable> MAPPINGS = Map.ofEntries(
			Map.entry(GraphProcessingSteps.EDIT_MODEL, this::executeEditModel),
			Map.entry(GraphProcessingSteps.NODE_CREATION, this::executeNodeCreation),
			Map.entry(GraphProcessingSteps.EDGE_CREATION, this::executeEdgeCreation),
			Map.entry(GraphProcessingSteps.CALC_WEIGHT, this::executeWeightCalculation),
			Map.entry(GraphProcessingSteps.SOLVE_CLUSTER, this::executeSolveCluster),
			Map.entry(GraphProcessingSteps.SAVE_RESULT, this::executeFinishedCluster));

	@Option(name = "-visualization", usage = "value for enabling the graphical interface")
	private boolean visualization;

	@Option(name = "-editModel", usage = "value for enabling the editing step of the model")
	private boolean editModel;

	@Option(name = "-debug", usage = "value for enabling debug mode")
	private boolean debug;

	@Option(name = "-settingsFile", usage = "path to properties file with defined priorities/settings")
	private String pathToPropertiesFile;

	@Option(name = "-pathResultFile", usage = "path to result file with the solved cluster")
	private String pathResultFile;

	@Option(name = "-resultFile", usage = "name of the result file")
	private String resultFile;
	/** Loader for configuration file */
	private PropertiesLoader loader;
	/** Reference to the used model file of previous step */
	private File modelFile;
	/** Parsed model */
	private ModelRepresentation modelRepresentation;
	/** State machine of processing stepsl */
	private StateMachine<GraphProcessingSteps> stateMachine;
	/** Current step */
	private GraphProcessingSteps currentStep;
	/** Reference to the service of the servicesnipper */
	private ServiceSnipperService service;
	/** Reference to the visualization of the servicesnipper */
	private Visualizer visual;
	/** Result object */
	private Result result;

	public ServiceCutInterpreter() {
		this.stateMachine = new StateMachine<>(GraphProcessingSteps.class);
		this.currentStep = this.stateMachine.startState();
		this.service = new ServiceSnipperServiceImpl();
	}

	@Override
	public Object run(String input) {
		initialize(input);
		try {
			execute();
		} catch (Exception e) {
			throw new MigrationToolRuntimeException(e.getMessage(), e);
		}
		// No Step afterwards
		StartUp.shutdown();
		return null;
	}

	private void initialize(String input) {
		if ((this.pathToPropertiesFile == null) && !this.visualization) {
			throw new MigrationToolInitException("Priorties file needed");
		}
		this.modelFile = new File(input);
		this.modelRepresentation = createModel();
		this.pathResultFile = this.pathResultFile == null ? "" : this.pathResultFile;
		this.resultFile = this.resultFile == null ? "result" : this.resultFile;
		this.visual = this.visualization ? new ServiceSnipperVisualizer() : new VisualizerAdapter();
		if (!this.visualization) {
			this.loader = new PropertiesLoader(this.pathToPropertiesFile);
			this.loader.loadProps(false);
		}
	}

	private ModelRepresentation createModel() {
		ModelRepresentation model = JsonConverter.readJsonFromFile(this.modelFile, ModelRepresentation.class);
		ModelValidator.validate(model);
		return model;
	}

	private ModelRepresentation createModel(String json) {
		ModelRepresentation model = JsonConverter.readJson(json, ModelRepresentation.class);
		ModelValidator.validate(model);
		return model;
	}

	private boolean awaitUserInteraction() {
		this.visual.setProgress(this.currentStep.name(), this.stateMachine.getProcentOfProgress());
		this.visual.awaitApproval(this.currentStep);
		return false;
	}

	private boolean awaitUserInteraction(StateMachine<?> subProcess) {
		Enum<?> current = subProcess.getCurrent();
		String s = this.currentStep.name() + "/" + current.name();
		int value = this.stateMachine.getProcentOfProgress(current.ordinal(), subProcess.processCount());
		this.visual.setProgress(s, value);
		return this.visual.awaitApproval(this.currentStep);
	}

	private void execute() {
		LOG.info("Debug is " + (this.debug ? "active, show each step" : "deactived, only necessary steps are shown"));
		do {
			LOG.info("Current Process step: " + this.currentStep.toString());
			LOG.info("Current Process step has substeps? " + this.currentStep.hasSubProcess());
			this.MAPPINGS.get(this.currentStep).run();
			LOG.info("Finished Process step: " + this.currentStep.toString());
			this.currentStep = this.stateMachine.nextStep();
		} while (!this.stateMachine.isProcessDone());
		LOG.info("Finished Process");
		this.visual.stop();
	}

	private void executeEditModel() {
		if (!this.editModel || !this.visualization) {
			LOG.warn("Edit Model step is skipped");
			return;
		}
		// Repeat step until model is valid again
		boolean valid = true;
		ModelRepresentation copyBeforeEditing = createModel();
		String jsonStringAfterEditing = null;
		do {
			LOG.info("Start visualization of " + this.modelFile.getName());
			this.visual.visualizeModel(JsonConverter.toJsonString(copyBeforeEditing),
					JsonConverter.toJsonString(copyBeforeEditing));
			awaitUserInteraction();
			// Reload model
			try {
				jsonStringAfterEditing = this.visual.getEditedModel();
				this.modelRepresentation = createModel(jsonStringAfterEditing);
				valid = true;
			} catch (Exception e) {
				LOG.error("Validation ERROR" + e.getMessage());
				valid = false;
				LOG.info("Validation ERROR" + e.getMessage());
			}
		} while (!valid);
	}

	private void executeNodeCreation() {
		LOG.info("Import Architecture from file: " + this.modelFile.getName());
		this.service.importArchitecture(this.modelRepresentation);
		LOG.info("Import Architecture successfull");
		if (this.debug) {
			this.visual.visualizeGraph(this.service.getCurrentGraphState());
			awaitUserInteraction();
		}
	}

	private void executeEdgeCreation() {
		StateMachine<?> state = this.currentStep.subProcess();
		Enum<?> substep = state.startState();
		do {
			this.service.process(this.currentStep, substep);
			if (this.debug) {
				this.visual.visualizeGraph(this.service.getCurrentGraphState());
				awaitUserInteraction(state);
			}
			substep = state.nextStep();
		} while (!state.finishedState().equals(substep));
	}

	private void executeWeightCalculation() {
		StateMachine<?> state = this.currentStep.subProcess();
		Enum<?> substep = state.startState();
		do {
			this.service.process(this.currentStep, substep);
			if (this.debug) {
				this.visual.visualizeGraph(this.service.getCurrentGraphState());
				awaitUserInteraction(state);
			}
			substep = state.nextStep();
		} while (!state.finishedState().equals(substep));
	}

	private void executeSolveCluster() {
		executeSolveCluster(false);
	}

	private void executeSolveCluster(boolean skip) {
		LOG.info("Import Priorities from " + (this.visualization ? "GUI" : "File"));
		if (!this.debug) {
			this.visual.visualizeGraph(this.service.getCurrentGraphState());
		}
		// Settings from GUI
		if (!skip) {
			awaitUserInteraction();
		}
		ResolverConfiguration configuration = this.visual.getSettings();
		if (configuration == null) {
			// Settings from File
			configuration = new ResolverConfiguration();
			for (Entry<String, String> entry : this.loader.getCache().entrySet()) {
				String key = entry.getKey().toUpperCase();
				try {
					// priority
					CouplingCriteria priority = CouplingCriteria.valueOf(key);
					configuration.getPriorities().put(priority, Priorities.valueOf(entry.getValue().toUpperCase()));
				} catch (Exception e) {
					// algorithmn setting
					if (key.equals(SolverConfiguration.ALGORITHMN)) {
						configuration.setSelectedAlgorithmn(ClusterAlgorithms.valueOf(entry.getValue().toUpperCase()));
					} else {
						configuration.getSettings().put(key, entry.getValue());
					}
				}
			}
		}
		if (configuration.getPriorities().size() != CouplingCriteria.values().length) {
			throw new MigrationToolRuntimeException("Priority list is not complete");
		}
		if (configuration.getSelectedAlgorithmn() == null) {
			throw new MigrationToolRuntimeException("No Algorithmn selected");
		}
		SolverConfiguration config = new SolverConfiguration();
		config.getConfig().putAll(configuration.getSettings());
		this.result = this.service.solveCluster(configuration.getSelectedAlgorithmn(), config,
				configuration.getPriorities());
		LOG.info("Solve cluster successfull");
		this.visual.visualizeCluster(this.service.getCurrentResultGraphState());
	}

	private void executeFinishedCluster() {
		boolean undo = awaitUserInteraction();
		if (undo) {
			LOG.info("User calculates new cluster");
			this.currentStep = this.stateMachine.previousStep();
			executeSolveCluster(true);
		} else {
			File res = new File(this.pathResultFile + this.resultFile + ".json");
			if (res.exists()) {
				LOG.warn("Override: " + res.getName());
				res.delete();
			}
			try {
				res.createNewFile();
				JsonConverter.toJsonString(res, this.result);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}
