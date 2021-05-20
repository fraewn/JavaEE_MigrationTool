package application;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;

import controllers.Controller;
import controllers.GraphVisualController;
import controllers.MainController;
import data.GenericDTO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.AdjacencyMatrix;

public class Appl extends Application {

	private static final CountDownLatch LATCH = new CountDownLatch(1);

	private static Appl appl;

	public static final String RESOURCE_BUNDLE = "gui.resources.strings";

	public static Locale language = Locale.ENGLISH;

	public static DeployedControllers controllers;

	private CurrentView currentView;

	public static boolean approval;

	public Appl() {
		Appl.controllers = new DeployedControllers();
		// Start View
		this.currentView = CurrentView.MODEL;
	}

	public static Appl waitForAppl() {
		try {
			LATCH.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return appl;
	}

	public static void setAppl(Appl appl) {
		Appl.appl = appl;
		LATCH.countDown();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/fxml/configurator.fxml"));
		loader.setResources(ResourceBundle.getBundle(RESOURCE_BUNDLE, language));
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(primaryStage, root);
		decorator.setCustomMaximize(true);
		decorator.setGraphic(new SVGGlyph(""));

		Scene scene = new Scene(decorator, 800, 600);

		Appl.controllers.addController(MainController.class, loader.getController());

		ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
				JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
				Appl.class.getResource("/gui/css/jfoenix-components.css").toExternalForm(),
				Appl.class.getResource("/gui/css/jfoenix-main-demo.css").toExternalForm());

		String title = loader.getResources().getString("title");
		primaryStage.setTitle("JavaFX " + title);
		primaryStage.setScene(scene);
		primaryStage.show();
		setAppl(this);
	}

	public void visualizeModel(String jsonString) {
		Platform.runLater(() -> {
			try {
				if (!this.currentView.equals(CurrentView.MODEL)) {
					MainController c = Appl.controllers.getController(MainController.class);
					this.currentView = CurrentView.MODEL;
					c.update(new GenericDTO<>(this.currentView));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void visualizeGraph(AdjacencyMatrix matrix) {
		Platform.runLater(() -> {
			try {
				Controller c = Appl.controllers.getController(MainController.class);
				if (!this.currentView.equals(CurrentView.GRAPH)) {
					this.currentView = CurrentView.GRAPH;
					c.update(new GenericDTO<>(this.currentView));
				}
				Controller cv = Appl.controllers.getController(GraphVisualController.class);
				cv.update(new GenericDTO<>(matrix));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void setProgress(String label, int current) {
		Platform.runLater(() -> {
			try {
				Controller c = Appl.controllers.getController(MainController.class);
				c.update(new GenericDTO<>(Integer.valueOf(current)));
				c.update(new GenericDTO<>(label));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void shutdown() {
		Platform.exit();
	}
}
