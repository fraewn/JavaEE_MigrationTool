package recommender.service.gui;

import java.util.List;
import java.util.Map;

import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import visualization.VisualizationService;

/**
 * Service Interface of the visualization of the recommender engine
 */
public interface Visualizer extends VisualizationService<RecommenderProcessingSteps> {

	/**
	 * Visualize the current recommendations
	 *
	 * @param recommandations list of recommendations
	 * @param step            current step
	 */
	void visualizeModel(Map<String, Recommendation> recommandations, RecommenderProcessingSteps step);

	/**
	 * Get the changes which should be applied
	 *
	 * @return list recommendation
	 */
	List<Recommendation> getAppliedChanges();

}
