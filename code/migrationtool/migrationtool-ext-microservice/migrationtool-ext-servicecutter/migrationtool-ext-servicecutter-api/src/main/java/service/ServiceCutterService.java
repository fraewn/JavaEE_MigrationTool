package service;

import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import graph.processing.ProcessAutomate;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public interface ServiceCutterService {

	void importArchitecture(ModelRepresentation rep);

	void process(GraphProcessingSteps currentStep, ProcessAutomate<?> subProcess);

	Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities);

	AdjacencyList getCurrentGraphState();

	AdjacencyList getCurrentResultGraphState();
}
