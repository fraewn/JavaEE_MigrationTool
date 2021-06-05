package service.gui;

import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public interface Visualizer {

	void visualizeModel(String jsonString);

	void visualizeGraph(AdjacencyList adjList);

	void visualizeCluster(AdjacencyList adjList);

	void setProgress(String label, int progress);

	ClusterAlgorithms getSelectedAlgorithmn();

	Map<String, String> getSettings();

	Map<CouplingCriteria, Priorities> getPriorities();

	void undoStep(GraphProcessingSteps step);

	boolean awaitApproval(GraphProcessingSteps step);

	void stop();
}
