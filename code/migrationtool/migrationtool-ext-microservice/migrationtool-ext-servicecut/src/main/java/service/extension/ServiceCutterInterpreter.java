package service.extension;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import exceptions.GraphRuntimeException;
import exceptions.MigrationToolInitException;
import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import graph.processing.ProcessAutomate;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;
import operations.InterpreterService;
import service.LocalVisualizer;
import service.ServiceCutterService;
import service.ServiceCutterServiceImpl;
import service.gui.Visualizer;
import service.gui.VisualizerAdapter;
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

	@Option(name = "-resultFile", usage = "name of the result file")
	private String resultFile;

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
			throw new GraphRuntimeException(e.getMessage(), e);
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
		this.visual = this.visualization ? new LocalVisualizer() : new VisualizerAdapter();
		this.currentStep = GraphProcessingSteps.EDIT_MODEL;
		this.rep = JsonConverter.readJsonFromFile(this.file, ModelRepresentation.class);
		this.pathResultFile = this.pathResultFile == null ? "" : this.pathResultFile;
		this.resultFile = this.resultFile == null ? "result" : this.resultFile;
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
				executeSolveCluster(true);
				break;
			case SAVE_RESULT:
				executeFinishedCluster();
				break;
			default:
				break;
			}
			LOG.info("Finished Process step: " + this.currentStep.toString());
			this.currentStep = this.currentStep.nextStep();
		} while (!this.currentStep.isProcessDone());
		LOG.info("Finished Process");
		this.visual.stop();
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
				AdjacencyList adjList = this.imp.getCurrentGraphState();
				this.visual.visualizeGraph(adjList);
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
				AdjacencyList adjList = this.imp.getCurrentGraphState();
				this.visual.visualizeGraph(adjList);
				String s = this.currentStep.name() + "/" + substep.name();
				this.visual.setProgress(s, this.currentStep.getProcentOfProgress(substep.ordinal()));
				this.visual.awaitApproval(this.currentStep);
			}
			substep = substep.nextStep();
		} while (!this.currentStep.finishedState().equals(substep));
	}

	private void executeSolveCluster(boolean wait) {
		LOG.info("Import Priorities from " + (this.visualization ? "GUI" : "File"));
		Map<CouplingCriteria, Priorities> priorities = new HashMap<>();
		Map<String, String> settings = new HashMap<>();
		ClusterAlgorithms algorithmn = null;
		if (this.visualization) {
			// first show with no debug
			if (!this.debug) {
				this.visual.visualizeGraph(this.imp.getCurrentGraphState());
			}
			// Priorities from GUI
			String s = this.currentStep.name();
			this.visual.setProgress(s, this.currentStep.getProcentOfProgress());
			if (wait) {
				this.visual.awaitApproval(this.currentStep);
			}
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
			throw new GraphRuntimeException("Priority list is not complete");
		}
		if (algorithmn == null) {
			throw new GraphRuntimeException("No Algorithmn selected");
		}
		SolverConfiguration config = new SolverConfiguration();
		config.getConfig().putAll(settings);
		this.result = this.imp.solveCluster(algorithmn, config, priorities);
		LOG.info("Solve cluster successfull");
		this.visual.visualizeCluster(this.imp.getCurrentResultGraphState());
	}

	private void executeFinishedCluster() {
		String s = this.currentStep.name();
		this.visual.setProgress(s, this.currentStep.getProcentOfProgress());
		LOG.info("Wait for completion");
		boolean undo = this.visual.awaitApproval(this.currentStep);
		if (undo) {
			LOG.info("User calculates new cluster");
			this.currentStep = this.currentStep.previousStep();
			executeSolveCluster(false);
		} else {
			File res = new File(this.pathResultFile + this.resultFile + ".json");
			if (res.exists()) {
				LOG.warn("Override: " + res.getName());
				res.delete();
			}
			try {
				res.createNewFile();
				JsonConverter.getMapper().writeValue(res, this.result);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}
