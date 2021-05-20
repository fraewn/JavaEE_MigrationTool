package ui;

import java.util.Map;

import processing.GraphProcessingSteps;

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
}
