package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import data.CurrentView;
import data.GenericDTO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import processing.GraphProcessingSteps;
import visualization.ChildController;
import visualization.ParentController;

public class MainController implements ParentController {

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

	@FXML
	private ResourceBundle resources;

	private CurrentView currentView;

	private ChildController controllerContent;

	private Map<GraphProcessingSteps, Approval> approvementMap;

	public MainController() {
		this.approvementMap = new HashMap<>();
	}

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
	public void changeContent(CurrentView view) {
		if ((this.currentView == null) || !view.equals(this.currentView)) {
			this.currentView = view;
			try {
				URL sideMenuURL = getClass().getClassLoader().getResource(view.getLocationSideMenu());
				URL contentURL = getClass().getClassLoader().getResource(view.getLocationContent());
				FXMLLoader loaderSideMenu = new FXMLLoader(sideMenuURL, this.resources);
				FXMLLoader loaderContent = new FXMLLoader(contentURL, this.resources);
				Parent paneSideMenu = loaderSideMenu.load();
//				ChildController controllerSideMenu = loaderSideMenu.getController();
				Parent paneContent = loaderContent.load();
				this.controllerContent = loaderContent.getController();
				this.controllerContent.setParentController(this);
				this.drawer.setSidePane(paneSideMenu);
				this.drawer.setContent(paneContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setProgressStep(String step, int procent) {
		this.labelStep.setText(step);
		KeyFrame kfBar = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbar.progressProperty(), procent / 100.));
		KeyFrame kfLabel = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbarIndicator.textProperty(), "" + procent + "%"));
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().addAll(kfBar, kfLabel);
		timeline.play();
	}

	@Override
	public void refreshContent(GenericDTO<?> dto) {
		this.controllerContent.refreshContent(dto);
	}

	@Override
	public void needApproval(GraphProcessingSteps step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			a.approved = false;
			a.awaitApproval = true;
		} else {
			Approval a = new Approval();
			a.awaitApproval = true;
			this.approvementMap.put(step, a);
		}
		this.controllerContent.reachedProcessStep(step);
	}

	@Override
	public void approve(GraphProcessingSteps step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval) {
				a.approved = true;
			}
		}
	}

	@Override
	public boolean isApproved(GraphProcessingSteps step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval && a.approved) {
				a.approved = false; // fix Gui thread to slow
				return true;
			}
		}
		return false;
	}

	class Approval {
		boolean awaitApproval;
		boolean approved;
	}
}
