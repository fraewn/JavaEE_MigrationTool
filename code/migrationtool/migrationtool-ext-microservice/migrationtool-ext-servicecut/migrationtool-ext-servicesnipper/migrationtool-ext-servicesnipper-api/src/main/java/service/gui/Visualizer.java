package service.gui;

import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import visualization.VisualizationService;

public interface Visualizer extends VisualizationService<GraphProcessingSteps> {

	/**
	 * Visualize the current and edited model
	 *
	 * @param jsonStringBefore list of recommendations
	 * @param jsonStringAfter  current step
	 */
	void visualizeModel(String jsonStringBefore, String jsonStringAfter);

	void visualizeGraph(AdjacencyList adjList);

	void visualizeCluster(AdjacencyList adjList);

	String getEditedModel();

	ResolverConfiguration getSettings();
}
