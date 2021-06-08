package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import data.CurrentView;
import graph.clustering.ClusterAlgorithms;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;
import models.Configurations;
import visualization.ChildController;
import visualization.Controller;
import visualization.ParentController;

public class MainController implements ParentController, Controller {

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

	private ChildController controllerSideMenu;

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
				this.controllerSideMenu = loaderSideMenu.getController();
				this.controllerSideMenu.setParentController(this);
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
	public void visualizeModel(String jsonString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visualizeGraph(AdjacencyList adjList) {
		this.controllerContent.refreshModel(adjList);
	}

	@Override
	public void visualizeCluster(AdjacencyList adjList) {
		this.controllerContent.refreshModel(adjList);
	}

	@Override
	public void setProgress(String label, int progress) {
		this.labelStep.setText(label);
		KeyFrame kfBar = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbar.progressProperty(), progress / 100.));
		KeyFrame kfLabel = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbarIndicator.textProperty(), "" + progress + "%"));
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().addAll(kfBar, kfLabel);
		timeline.play();
	}

	@Override
	public ClusterAlgorithms getSelectedAlgorithmn() {
		Configurations config = this.controllerSideMenu.getModel();
		return config.getSelectedAlgorithmn().get();
	}

	@Override
	public Map<String, String> getSettings() {
		Configurations config = this.controllerSideMenu.getModel();
		Map<String, String> map = new HashMap<>();
		for (Entry<String, StringProperty> e : config.getAlgorithmnSettings().entrySet()) {
			map.put(e.getKey(), e.getValue().get());
		}
		return map;
	}

	@Override
	public Map<CouplingCriteria, Priorities> getPriorities() {
		Configurations config = this.controllerSideMenu.getModel();
		Map<CouplingCriteria, Priorities> map = new HashMap<>();
		for (Entry<CouplingCriteria, ObjectProperty<Priorities>> e : config.getBindValues().entrySet()) {
			map.put(e.getKey(), e.getValue().get());
		}
		return map;
	}

	@Override
	public void undoStep(GraphProcessingSteps step) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean awaitApproval(GraphProcessingSteps step) {
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
		this.controllerSideMenu.reachedProcessStep(step);
		return false;
	}

	@Override
	public void approve(GraphProcessingSteps step, boolean undo) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval) {
				a.approved = true;
				if (undo) {
					a.undo = true;
				}
			}
		}
	}

	@Override
	public boolean isUndo(GraphProcessingSteps step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval && a.undo) {
				a.undo = false; // fix Gui thread to slow
				return true;
			}
		}
		return false;
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
		boolean undo;
	}
}
