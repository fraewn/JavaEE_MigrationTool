package ui;

import java.util.Map;

import model.criteria.CouplingCriteria;
import model.data.Priorities;
import processing.GraphProcessingSteps;
import solver.ClusterAlgorithms;

public class VisualizerDummy implements Visualizer {

	@Override
	public void visualizeModel(String jsonString) {
	}

	@Override
	public void visualizeGraph(AdjacencyMatrix matrix) {
	}

	@Override
	public Map<String, String> getSettings() {
		return null;
	}

	@Override
	public void awaitApproval(GraphProcessingSteps step) {
	}

	@Override
	public void setProgress(String label, int progress) {
	}

	@Override
	public ClusterAlgorithms getSelectedAlgorithmn() {
		return null;
	}

	@Override
	public Map<CouplingCriteria, Priorities> getPriorities() {
		return null;
	}

	@Override
	public void visualizeCluster(AdjacencyMatrix matrix) {

	}

	@Override
	public void undoStep(GraphProcessingSteps step) {
		// TODO Auto-generated method stub

	}
}
