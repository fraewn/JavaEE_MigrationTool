package recommandation.controllers;

import java.util.ArrayList;
import java.util.Map;

import application.DefaultParentController;
import controllers.ChildController;
import javafx.fxml.FXML;
import model.data.ArchitectureInformation;
import recommendaton.model.Configuration;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import recommender.service.RecommenderService;

public class RecommendationController extends DefaultParentController<RecommenderProcessingSteps> {

	@FXML
	private ChildController<RecommenderProcessingSteps> visualController;

	@FXML
	private ChildController<RecommenderProcessingSteps> configController;

	private RecommenderService service;

	@Override
	public <T> void afterInitialize(T dto) {
		if (dto instanceof RecommenderService) {
			this.service = (RecommenderService) dto;
		}
		this.configController.setParentController(this);
		this.visualController.setParentController(this);
		this.children.add(this.configController);
		this.children.add(this.visualController);
	}

	@Override
	public <T> void beforeVisualize(T dto) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void visualize(T dto, RecommenderProcessingSteps step) {
		if (dto instanceof Map) {
			this.currentStep = step;
			Map<String, Recommendation> list = (Map<String, Recommendation>) dto;
			ArchitectureInformation ai = this.service.convertRecommendations(new ArrayList<>(list.values()), step);
			this.configController.reachedProcessStep(step);
			this.configController.beforeRefreshModel(this.service.getInformation(step));
			this.configController.refreshModel(list);
			this.visualController.refreshModel(ai);
		}
	}

	@Override
	public void callbackEvent() {
		Configuration model = this.configController.getModel();
		ArchitectureInformation info = this.service.convertRecommendations(model.getRecommendations(),
				this.currentStep);
		this.visualController.refreshModel(info);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		Configuration c = this.configController.getModel();
		return (T) c.getRecommendations();
	}

}
