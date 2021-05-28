package service;

import java.util.Map;

import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Priorities;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import solver.ClusterAlgorithms;
import solver.SolverConfiguration;
import ui.AdjacencyMatrix;

public interface ServiceCutterService {

	void importArchitecture(ModelRepresentation rep);

	void process(GraphProcessingSteps currentStep, ProcessAutomate<?> subProcess);

	Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities);

	AdjacencyMatrix getCurrentGraphState();

	AdjacencyMatrix getCurrentResultGraphState();
}
