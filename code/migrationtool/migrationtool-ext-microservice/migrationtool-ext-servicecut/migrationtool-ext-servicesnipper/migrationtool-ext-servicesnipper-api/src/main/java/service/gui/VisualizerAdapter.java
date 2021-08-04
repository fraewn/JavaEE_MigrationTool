package service.gui;

import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;

public class VisualizerAdapter implements Visualizer {

	@Override
	public void setProgress(String label, int progress) {

	}

	@Override
	public boolean awaitApproval(GraphProcessingSteps step) {
		return false;
	}

	@Override
	public void undoStep(GraphProcessingSteps step) {

	}

	@Override
	public void stop() {

	}

	@Override
	public void visualizeModel(String jsonStringBefore, String jsonStringAfter) {

	}

	@Override
	public void visualizeGraph(AdjacencyList adjList) {

	}

	@Override
	public void visualizeCluster(AdjacencyList adjList) {

	}

	@Override
	public String getEditedModel() {
		return null;
	}

	@Override
	public ResolverConfiguration getSettings() {
		return null;
	}
}
