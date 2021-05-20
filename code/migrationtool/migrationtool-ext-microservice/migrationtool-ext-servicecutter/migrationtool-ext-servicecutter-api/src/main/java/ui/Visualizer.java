package ui;

import java.util.Map;

import processing.GraphProcessingSteps;

public interface Visualizer {

	void visualizeModel(String jsonString);

	void visualizeGraph(AdjacencyMatrix matrix);

	void setProgress(String label, int progress);

	Map<String, String> getSettings();

	void awaitApproval(GraphProcessingSteps step);
}
