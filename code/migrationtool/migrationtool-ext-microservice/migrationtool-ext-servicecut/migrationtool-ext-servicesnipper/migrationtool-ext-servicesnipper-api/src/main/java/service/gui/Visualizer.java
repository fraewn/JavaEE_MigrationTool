package service.gui;

import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import model.Result;
import visualization.VisualizationService;

/**
 * Interaction interface with the visualization interface of the servicesnipper
 */
public interface Visualizer extends VisualizationService<GraphProcessingSteps> {

	/**
	 * Visualize the current and edited model
	 *
	 * @param jsonStringBefore list of recommendations
	 * @param jsonStringAfter  current step
	 */
	void visualizeModel(String jsonStringBefore, String jsonStringAfter);

	/**
	 * Visualize the current input graph
	 *
	 * @param adjList model of graph
	 */
	void visualizeGraph(GraphModel model);

	/**
	 * Visualize the current result graph
	 *
	 * @param result  result object
	 * @param adjList model of graph
	 */
	void visualizeCluster(Result result, GraphModel model);

	/**
	 * @return the editied model as json string
	 */
	String getEditedModel();

	/**
	 * @return current settings
	 */
	ResolverConfiguration getSettings();
}
