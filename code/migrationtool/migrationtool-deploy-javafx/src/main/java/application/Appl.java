package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.JFXDecorator;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Appl extends Application {

	public static final String RESOURCE_BUNDLE = "gui/resources/translations";

	public static Locale language = Locale.ENGLISH;

	public static List<String> loadedArgs = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		loadedArgs.addAll(getParameters().getRaw());
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/fxml/application.fxml"));
		loader.setResources(ResourceBundle.getBundle(RESOURCE_BUNDLE, language));
		Parent root = loader.load();

		JFXDecorator decorator = new JFXDecorator(primaryStage, root);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator, 800, 600);

		// @formatter:off
		String[] sheets = {
				JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
				JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
				Appl.class.getResource("/gui/css/component.css").toExternalForm()
		};
		// @formatter:on

		ObservableList<String> stylesheets = scene.getStylesheets();
		stylesheets.addAll(sheets);
		primaryStage.setTitle(loader.getResources().getString("app.title"));
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
