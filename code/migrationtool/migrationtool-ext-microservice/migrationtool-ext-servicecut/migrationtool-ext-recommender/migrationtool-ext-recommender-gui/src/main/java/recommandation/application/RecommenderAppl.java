package recommandation.application;

import java.util.Locale;

import application.Appl;
import javafx.stage.Stage;
import recommender.processing.RecommenderProcessingSteps;
import recommender.service.RecommenderService;

public class RecommenderAppl extends Appl<RecommenderProcessingSteps> {

	private static final String RESOURCE_BUNDLE = "gui/resources/rec-translations";

	private static final String FXML = "gui/fxml/recommender.fxml";

	private RecommenderService service;

	public RecommenderAppl(RecommenderService service) {
		super(Locale.ENGLISH, RESOURCE_BUNDLE, FXML);
		this.service = service;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		this.mainController.afterInitialize(this.service);
	}
}
