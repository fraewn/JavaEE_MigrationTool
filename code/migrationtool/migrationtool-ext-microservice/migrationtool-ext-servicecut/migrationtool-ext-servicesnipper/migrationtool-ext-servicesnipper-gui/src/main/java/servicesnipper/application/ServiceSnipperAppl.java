package servicesnipper.application;

import java.util.Locale;

import application.Appl;
import graph.processing.GraphProcessingSteps;
import javafx.stage.Stage;

public class ServiceSnipperAppl extends Appl<GraphProcessingSteps> {

	private static final String RESOURCE_BUNDLE = "gui/resources/translations";

	private static final String FXML = "gui/fxml/servicesnipper.fxml";

	public ServiceSnipperAppl() {
		super(Locale.ENGLISH, RESOURCE_BUNDLE, FXML);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		this.mainController.afterInitialize(null);
	}
}
