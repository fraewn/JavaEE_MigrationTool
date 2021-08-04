package application;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;

import controllers.MainControl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Create a javaFX based Gui to show the defined process
 *
 * @param <E> The Processing step enum
 */
public abstract class Appl<E extends Enum<E>> extends Application {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Default location of the resource bundle */
	public static final String RESOURCE_BUNDLE = "gui/resources/translations";
	/** Default language */
	public static final Locale LANGUAGE = Locale.ENGLISH;
	/** Default fxml file to load */
	public static final String FXML = "gui/fxml/configurator.fxml";
	/** Current resource bundle */
	private String resources;
	/** Current fxml file */
	private String fxmlFile;
	/** Current language */
	private Locale lang;
	/** main controller */
	protected MainControl<E> mainController;
	/** current window */
	private Stage currentStage;

	public Appl() {
		this.lang = LANGUAGE;
		this.resources = RESOURCE_BUNDLE;
		this.fxmlFile = FXML;
	}

	public Appl(Locale language, String resources, String fxml) {
		this.lang = language;
		this.resources = resources;
		this.fxmlFile = fxml;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.currentStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.fxmlFile));
		loader.setResources(ResourceBundle.getBundle(this.resources, this.lang));
		Parent root = loader.load();
		this.mainController = loader.getController();

		JFXDecorator decorator = new JFXDecorator(primaryStage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator, 800, 600);

		String[] sheets = {
				JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
				JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
				Appl.class.getResource("/gui/css/component.css").toExternalForm()
		};

		ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.addAll(sheets);

		String title = loader.getResources().getString("title");
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
		LOG.info("Application started");
	}

	/**
	 * Visualize the passed dto object
	 *
	 * @param <T>  dto
	 * @param dto  object
	 * @param step step
	 */
	public <T> void visualize(T dto, E step) {
		Platform.runLater(() -> {
			this.mainController.visualize(dto, step);
		});
	}

	/**
	 * Pass a argument before the visualization method gets triggered
	 *
	 * @param <B>       dto before visualization
	 * @param <T>       dto of visualization
	 * @param beforeDTO dto object
	 * @param visualDTO dto object
	 * @param step      step
	 */
	public <B, T> void visualize(B beforeDTO, T visualDTO, E step) {
		Platform.runLater(() -> {
			this.mainController.beforeVisualize(beforeDTO);
			this.mainController.visualize(visualDTO, step);
		});
	}

	/**
	 * Show the current step name and the overall progress
	 *
	 * @param label   String text
	 * @param current
	 */
	public void setProgress(String label, int current) {
		Platform.runLater(() -> {
			this.mainController.setProgress(label, current);
		});
	}

	/**
	 * Migrationtool is waiting for an user action
	 *
	 * @param step processing step
	 */
	public void awaitApproval(E step) {
		LOG.info("Process awaits approval of step {}", step);
		Platform.runLater(() -> {
			this.mainController.awaitApproval(step);
		});
	}

	/**
	 * Checks if the defined processing step is already approved
	 *
	 * @param step processing step
	 * @return processing approved
	 */
	public boolean isApproved(E step) {
		if (this.mainController == null) {
			return false;
		}
		return this.mainController.isApproved(step);
	}

	/**
	 * Checks if the defined processing is accepted
	 *
	 * @param step processing step
	 * @return processing undo
	 */
	public boolean isUndo(E step) {
		if (this.mainController == null) {
			return false;
		}
		return this.mainController.isUndo(step);
	}

	/**
	 * Gets the overall state of the gui
	 *
	 * @param <T> dto
	 * @return dto object
	 */
	public <T> T getModel() {
		if (this.mainController == null) {
			return null;
		}
		return this.mainController.getModel();
	}

	/**
	 * Stops the application
	 */
	public void shutdown() {
		LOG.info("End Application");
		Platform.runLater(() -> {
			this.currentStage.close();
		});
	}
}
