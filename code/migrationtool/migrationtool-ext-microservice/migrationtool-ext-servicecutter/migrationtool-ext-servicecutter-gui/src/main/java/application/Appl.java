package application;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;

import data.CurrentView;
import graph.clustering.ClusterAlgorithms;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;
import visualization.Controller;

public class Appl extends Application {

	public static final String RESOURCE_BUNDLE = "gui/resources/translations";

	public static Locale language = Locale.ENGLISH;

	private Controller mainController;

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
			this.mainController.visualizeModel(jsonString);
		});
	}

	public void visualizeGraph(AdjacencyList adjList) {
		Platform.runLater(() -> {
			this.mainController.changeContent(CurrentView.GRAPH);
			this.mainController.visualizeGraph(adjList);
		});
	}

	public void visualizeCluster(AdjacencyList adjList) {
		Platform.runLater(() -> {
			this.mainController.changeContent(CurrentView.GRAPH);
			this.mainController.visualizeCluster(adjList);
		});
	}

	public void setProgress(String label, int current) {
		Platform.runLater(() -> {
			this.mainController.setProgress(label, current);
		});
	}

	public void awaitApproval(GraphProcessingSteps step) {
		Platform.runLater(() -> {
			this.mainController.awaitApproval(step);
		});
	}

	public boolean isApproved(GraphProcessingSteps step) {
		if (this.mainController == null) {
			return false;
		}
		return this.mainController.isApproved(step);
	}

	public boolean isUndo(GraphProcessingSteps step) {
		if (this.mainController == null) {
			return false;
		}
		return this.mainController.isUndo(step);
	}

	public Map<CouplingCriteria, Priorities> getPriorities() {
		if (this.mainController == null) {
			return null;
		}
		return this.mainController.getPriorities();
	}

	public ClusterAlgorithms getSelectedAlgorithmn() {
		if (this.mainController == null) {
			return null;
		}
		return this.mainController.getSelectedAlgorithmn();
	}

	public Map<String, String> getSettings() {
		if (this.mainController == null) {
			return null;
		}
		return this.mainController.getSettings();
	}

	public void shutdown() {
		Platform.exit();
	}
}
