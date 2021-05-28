package application;

import java.util.Locale;
import java.util.ResourceBundle;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;

import data.CurrentView;
import data.GenericDTO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import processing.GraphProcessingSteps;
import ui.AdjacencyMatrix;
import visualization.ParentController;

public class Appl extends Application {

	public static final String RESOURCE_BUNDLE = "gui/resources/translations";

	public static Locale language = Locale.ENGLISH;

	private ParentController mainController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/fxml/configurator.fxml"));
		loader.setResources(ResourceBundle.getBundle(RESOURCE_BUNDLE, language));
		Parent root = loader.load();
		this.mainController = loader.getController();

		JFXDecorator decorator = new JFXDecorator(primaryStage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator, 800, 600);

		// @formatter:off
		String[] sheets = {
				JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
				JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
//				Appl.class.getResource("/gui/css/jfoenix-components.css").toExternalForm(),
//				Appl.class.getResource("/gui/css/jfoenix-main-demo.css").toExternalForm(),
				Appl.class.getResource("/gui/css/component.css").toExternalForm()
		};
		// @formatter:on

		ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.addAll(sheets);

		String title = loader.getResources().getString("title");
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void visualizeModel(String jsonString) {
		Platform.runLater(() -> {
			this.mainController.changeContent(CurrentView.GRAPH);
//			this.mainController.refreshContent(new GenericDTO<>(matrix));
		});
	}

	public void visualizeGraph(AdjacencyMatrix matrix) {
		Platform.runLater(() -> {
			this.mainController.changeContent(CurrentView.GRAPH);
			this.mainController.refreshContent(new GenericDTO<>(matrix));
		});

	}

	public void setProgress(String label, int current) {
		Platform.runLater(() -> {
			this.mainController.setProgressStep(label, current);
		});
	}

	public void awaitApproval(GraphProcessingSteps step) {
		Platform.runLater(() -> {
			this.mainController.needApproval(step);
		});
	}

	public boolean isApproved(GraphProcessingSteps step) {
		if (this.mainController == null) {
			System.out.println("null");
			return false;
		}
		return this.mainController.isApproved(step);
	}

	public void shutdown() {
		Platform.exit();
	}
}
