package recommender.service.gui;

import java.util.List;
import java.util.Map;

import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;

/**
 * Dummy Implementation
 */
public class VisualizerAdapter implements Visualizer {

	@Override
	public void visualizeModel(Map<String, Recommendation> recommandations, RecommenderProcessingSteps step) {

	}

	@Override
	public List<Recommendation> getAppliedChanges() {
		return null;
	}

	@Override
	public void setProgress(String label, int progress) {

	}

	@Override
	public boolean awaitApproval(RecommenderProcessingSteps step) {
		return false;
	}

	@Override
	public void stop() {

	}

	@Override
	public void undoStep(RecommenderProcessingSteps step) {
		// TODO Auto-generated method stub

	}

}
