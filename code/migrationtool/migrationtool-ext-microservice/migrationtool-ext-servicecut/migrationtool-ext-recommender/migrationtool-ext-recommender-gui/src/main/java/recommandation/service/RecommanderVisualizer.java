package recommandation.service;

import java.util.List;
import java.util.Map;

import application.Appl;
import application.StartUp;
import recommandation.application.RecommenderAppl;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import recommender.service.RecommenderService;
import recommender.service.gui.Visualizer;

public class RecommanderVisualizer implements Visualizer {

	private Appl<RecommenderProcessingSteps> appl;

	private RecommenderService service;

	public RecommanderVisualizer(RecommenderService service) {
		this.service = service;
		this.appl = new RecommenderAppl(this.service);
		StartUp.startUp(this.appl);
	}

	@Override
	public void visualizeModel(Map<String, Recommendation> recommandations, RecommenderProcessingSteps step) {
		try {
			this.appl.visualize(recommandations, step);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean awaitApproval(RecommenderProcessingSteps step) {
		try {
			this.appl.awaitApproval(step);
			while (!this.appl.isApproved(step)) {
				Thread.sleep(500);
			}
			return this.appl.isUndo(step);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void undoStep(RecommenderProcessingSteps step) {

	}

	@Override
	public List<Recommendation> getAppliedChanges() {
		return this.appl.getModel();
	}

	@Override
	public void setProgress(String label, int progress) {
		try {
			this.appl.setProgress(label, progress);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		this.appl.shutdown();
	}
}
