package service.gui;

import java.util.Map;

import graph.clustering.ClusterAlgorithms;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public class VisualizerAdapter implements Visualizer {

	@Override
	public void visualizeModel(String jsonString) {

	}

	@Override
	public void visualizeGraph(AdjacencyList adjList) {

	}

	@Override
	public void visualizeCluster(AdjacencyList adjList) {

	}

	@Override
	public void setProgress(String label, int progress) {

	}

	@Override
	public ClusterAlgorithms getSelectedAlgorithmn() {
		return null;
	}

	@Override
	public Map<String, String> getSettings() {
		return null;
	}

	@Override
	public Map<CouplingCriteria, Priorities> getPriorities() {
		return null;
	}

	@Override
	public void undoStep(GraphProcessingSteps step) {

	}

	@Override
	public boolean awaitApproval(GraphProcessingSteps step) {
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
