package service.gui;

import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import model.Result;

/**
 * Dummy Implementation
 */
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
	public void visualizeGraph(GraphModel model) {

	}

	@Override
	public void visualizeCluster(Result result, GraphModel model) {

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
