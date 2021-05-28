package ui;

import java.util.Map;

import model.criteria.CouplingCriteria;
import model.data.Priorities;
import processing.GraphProcessingSteps;
import solver.ClusterAlgorithms;

public interface Visualizer {

	void visualizeModel(String jsonString);

	void visualizeGraph(AdjacencyMatrix matrix);

	void visualizeCluster(AdjacencyMatrix matrix);

	void setProgress(String label, int progress);

	ClusterAlgorithms getSelectedAlgorithmn();

	Map<String, String> getSettings();

	Map<CouplingCriteria, Priorities> getPriorities();

	void undoStep(GraphProcessingSteps step);

	void awaitApproval(GraphProcessingSteps step);
}
