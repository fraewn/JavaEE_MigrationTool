package servicesnipper.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;

import application.DefaultParentController;
import controllers.ChildController;
import exceptions.GraphInitException;
import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import model.ModelRepresentation;
import model.Result;
import servicesnipper.model.CurrentView;
import servicesnipper.model.Model;
import utils.JsonConverter;

public class ServiceSnipperController extends DefaultParentController<GraphProcessingSteps> {

	@FXML
	private StackPane stackPane;

	@FXML
	private StackPane titleBurgerContainer;

	@FXML
	private JFXHamburger titleBurger;

	@FXML
	private JFXDrawer drawer;

	private ChildController<GraphProcessingSteps> controllerSideMenu;

	private ChildController<GraphProcessingSteps> controllerContent;

	private CurrentView currentView;

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
	public <T> void afterInitialize(T dto) {
		if (this.currentView == null) {
			changeContent(CurrentView.MODEL);
		}
	}

	@Override
	public <T> void beforeVisualize(T dto) {
		if (dto instanceof Result) {
			Result result = (Result) dto;
			if (this.controllerContent != null) {
				this.controllerContent.beforeRefreshModel(result);
			}
		}
	}

	@Override
	public <T> void visualize(T dto, GraphProcessingSteps step) {
		if ((dto instanceof Model) && !this.currentView.equals(CurrentView.MODEL)) {
			changeContent(CurrentView.MODEL);
		}
		if ((dto instanceof GraphModel) && !this.currentView.equals(CurrentView.GRAPH)) {
			changeContent(CurrentView.GRAPH);
		}
		this.controllerSideMenu.refreshModel(dto);
		this.controllerContent.refreshModel(dto);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		if (this.currentView.equals(CurrentView.MODEL)) {
			Model m = this.controllerContent.getModel();
			return (T) m.getJsonStringAfter();
		}
		return this.controllerSideMenu.getModel();
	}

	private void changeContent(CurrentView view) {
		if ((this.currentView == null) || !view.equals(this.currentView)) {
			this.currentView = view;
			this.children.clear();
			try {
				this.controllerSideMenu = loadFXML(view.getLocationSideMenu(), x -> this.drawer.setSidePane(x));
				this.controllerContent = loadFXML(view.getLocationContent(), x -> this.drawer.setContent(x));
			} catch (IOException e) {
				throw new GraphInitException(e.getMessage());
			}
		}
	}

	private ChildController<GraphProcessingSteps> loadFXML(String url, Consumer<Parent> function) throws IOException {
		URL urlFXML = getClass().getClassLoader().getResource(url);
		FXMLLoader loader = new FXMLLoader(urlFXML, this.resources);
		Parent pane = loader.load();
		function.accept(pane);
		ChildController<GraphProcessingSteps> controller = loader.getController();
		if (controller != null) {
			controller.afterInitialization(this.stackPane);
			controller.setParentController(this);
			this.children.add(controller);
		}
		return controller;
	}

	@Override
	public void callbackEvent(ChildController<GraphProcessingSteps> caller) {
		if (this.currentView.equals(CurrentView.MODEL)) {
			if (caller.equals(this.controllerContent)) {
				Model fromController = this.controllerContent.getModel();
				visualize(fromController, this.currentStep);
			}
			if (caller.equals(this.controllerSideMenu)) {
				ModelRepresentation model = this.controllerSideMenu.getModel();
				Model fromController = this.controllerContent.getModel();
				Model newModel = new Model(fromController.getJsonStringBefore(), JsonConverter.toJsonString(model));
				visualize(newModel, this.currentStep);
			}
		}
	}
}
