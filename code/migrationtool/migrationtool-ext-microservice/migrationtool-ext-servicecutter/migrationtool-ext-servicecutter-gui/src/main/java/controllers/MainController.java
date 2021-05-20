package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import application.Appl;
import application.CurrentView;
import data.GenericDTO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainController implements Controller {

	@FXML
	private BorderPane mainView;

	@FXML
	private StackPane titleBurgerContainer;

	@FXML
	private JFXHamburger titleBurger;

	@FXML
	private JFXDrawer drawer;

	@FXML
	private Label labelStep;

	@FXML
	private Label progressbarIndicator;

	@FXML
	private ProgressBar progressbar;

	@Override
	@FXML
	public void initialize() {
		this.drawer.setOnDrawerOpening(e -> {
			final Transition animation = this.titleBurger.getAnimation();
			animation.setRate(1);
			animation.play();
		});
		this.drawer.setOnDrawerClosing(e -> {
			final Transition animation = this.titleBurger.getAnimation();
			animation.setRate(-1);
			animation.play();
		});
		this.titleBurgerContainer.setOnMouseClicked(e -> {
			if (this.drawer.isClosed() || this.drawer.isClosing()) {
				this.drawer.open();
			} else {
				this.drawer.close();
			}
		});
	}

	@Override
	public void update(GenericDTO<?> dto) throws Exception {
		if (dto.getObject() instanceof CurrentView) {
			CurrentView currentView = (CurrentView) dto.getObject();
			ResourceBundle resources = ResourceBundle.getBundle(Appl.RESOURCE_BUNDLE, Appl.language);
			URL sideMenu = getClass().getClassLoader().getResource(currentView.getLocationSideMenu());
			URL content = getClass().getClassLoader().getResource(currentView.getLocationContent());
			FXMLLoader loaderSideMenu = new FXMLLoader(sideMenu, resources);
			Pane paneConfig = loaderSideMenu.load();
			Controller c1 = loaderSideMenu.getController();
			if (c1 != null) {
				Appl.controllers.addController(c1.getClass(), c1);
			}
			FXMLLoader loaderContent = new FXMLLoader(content, resources);
			SplitPane paneCenter = loaderContent.load();
			Controller c2 = loaderContent.getController();
			if (c2 != null) {
				Appl.controllers.addController(c2.getClass(), c2);
			}
			this.drawer.setSidePane(paneConfig);
			this.drawer.setContent(paneCenter);
		} else if (dto.getObject() instanceof Integer) {
			Integer newProgress = (Integer) dto.getObject();
			int label = newProgress;
			KeyFrame kfBar = new KeyFrame(new Duration(3000),
					new KeyValue(this.progressbar.progressProperty(), newProgress / 100.));
			KeyFrame kfLabel = new KeyFrame(new Duration(3000),
					new KeyValue(this.progressbarIndicator.textProperty(), "" + label + "%"));
			Timeline timeline = new Timeline();
			timeline.getKeyFrames().addAll(kfBar, kfLabel);
			timeline.play();
		} else if (dto.getObject() instanceof String) {
			String newProgress = (String) dto.getObject();
			this.labelStep.setText(newProgress);
		}
	}
}
