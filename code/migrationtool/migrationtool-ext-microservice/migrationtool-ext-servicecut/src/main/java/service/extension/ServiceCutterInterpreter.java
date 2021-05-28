package service.extension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Priorities;
import operations.InterpreterService;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import service.LocalVisualizer;
import service.ServiceCutterService;
import service.ServiceCutterServiceImpl;
import solver.ClusterAlgorithms;
import solver.SolverConfiguration;
import ui.AdjacencyMatrix;
import ui.Visualizer;
import ui.VisualizerDummy;
import utils.JsonConverter;
import utils.PropertiesLoader;

public class ServiceCutterInterpreter extends InterpreterService<String, Object> {

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

	@Option(name = "-algorithmnFile", usage = "path to properties file with the used algorithmn")
	private String pathToAlgorithmn;

	@Option(name = "-pathResultFile", usage = "path to result file with the solved cluster")
	private String pathResultFile;

	private PropertiesLoader loaderAlgo;

	private PropertiesLoader loaderPrio;

	private File file;

	private ModelRepresentation rep;

	private GraphProcessingSteps currentStep;

	private ServiceCutterService imp;

	private Visualizer visual;

	private Result result;

	@Override
	public Object run(String input) {
		initialize(input);
		try {
			execute();
		} catch (Exception e) {
			throw new MigrationToolRuntimeException(e.getMessage(), e);
		}
		return null;
	}

	private void initialize(String input) {
		if ((this.pathToPropertiesFile == null) && !this.visualization) {
			throw new MigrationToolInitException("Priorties file needed");
		}
		if ((this.pathToAlgorithmn == null) && !this.visualization) {
			throw new MigrationToolInitException("Algorithmn file needed");
		}
		this.file = new File(input);
		this.imp = new ServiceCutterServiceImpl();
		this.visual = this.visualization ? new LocalVisualizer() : new VisualizerDummy();
		this.currentStep = GraphProcessingSteps.EDIT_MODEL;
		this.rep = JsonConverter.readJsonFromFile(this.file, ModelRepresentation.class);
		this.pathResultFile = this.pathResultFile == null ? "" : this.pathResultFile;
		if (!this.visualization) {
			this.loaderPrio = new PropertiesLoader(this.pathToPropertiesFile);
			this.loaderPrio.loadProps(false);
			this.loaderAlgo = new PropertiesLoader(this.pathToAlgorithmn);
			this.loaderAlgo.loadProps(false);
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
				executeSolveCluster();
				break;
			case FINISHED:
				executeFinishedCluster();
				break;
			default:
				break;
			}
			LOG.info("Finished Process step: " + this.currentStep.toString());
			this.currentStep = this.currentStep.nextStep();
		} while (!this.currentStep.isProcessDone());
	}

	private void executeFinishedCluster() {

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

	private void executeSolveCluster() {
		LOG.info("Import Priorities from " + (this.visualization ? "GUI" : "File"));
		Map<CouplingCriteria, Priorities> priorities = new HashMap<>();
		Map<String, String> settings = new HashMap<>();
		ClusterAlgorithms algorithmn = null;
		if (this.visualization) {
			// Priorities from GUI
			String s = this.currentStep.name();
			this.visual.setProgress(s, this.currentStep.getProcentOfProgress());
			this.visual.awaitApproval(this.currentStep);
			priorities.putAll(this.visual.getPriorities());
			settings.putAll(this.visual.getSettings());
			algorithmn = this.visual.getSelectedAlgorithmn();
		} else {
			// Priorities from File
			for (Entry<String, String> entry : this.loaderPrio.getCache().entrySet()) {
				priorities.put(CouplingCriteria.valueOf(entry.getKey().toUpperCase()),
						Priorities.valueOf(entry.getValue().toUpperCase()));
			}
			for (Entry<String, String> entry : this.loaderAlgo.getCache().entrySet()) {
				if (entry.getKey().toUpperCase().equals(SolverConfiguration.ALGORITHMN)) {
					algorithmn = ClusterAlgorithms.valueOf(entry.getValue().toUpperCase());
				} else {
					settings.put(entry.getKey().toUpperCase(), entry.getValue());
				}
			}
		}
		if (priorities.size() != CouplingCriteria.values().length) {
			throw new MigrationToolRuntimeException("Priority list is not complete");
		}
		if (algorithmn == null) {
			throw new MigrationToolRuntimeException("No Algorithmn selected");
		}
		SolverConfiguration config = new SolverConfiguration();
		config.getConfig().putAll(settings);
		this.imp.solveCluster(algorithmn, config, priorities);
		LOG.info("Solve cluster successfull");
		if (this.visualization) {
			this.visual.visualizeCluster(this.imp.getCurrentGraphState());
		}
	}
}
