package service;

import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

/**
 * Service interface to control the servicesnipper service
 */
public interface ServiceSnipperService {

	/**
	 * Import the architecture to start the process
	 */
	void importArchitecture(ModelRepresentation rep);

	/**
	 * Execute a process step on the current graph
	 *
	 * @param currentStep current step
	 * @param subProcess  current sub sequence step
	 */
	void process(GraphProcessingSteps currentStep, Enum<?> subProcess);

	/**
	 * Create a service cut with the provided community algorithm
	 *
	 * @param algo       the community algorithm
	 * @param config     setting of the algorithm
	 * @param priorities priorities of the coupling criteria
	 * @return service cut
	 */
	Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities);

	/**
	 * @return current state of the input graph
	 */
	GraphModel getCurrentGraphState();

	/**
	 * @return current state of the result graph
	 */
	GraphModel getCurrentResultGraphState();
}
