package servicesnipper.controllers;

import org.graphstream.ui.fx_viewer.FxViewPanel;

import com.jfoenix.controls.JFXButton;

import controllers.ChildController;
import controllers.ParentController;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import servicesnipper.views.GraphView;
import servicesnipper.views.GraphViewer;
import utils.LayoutUtils;

public class GraphVisualController implements ChildController<GraphProcessingSteps> {

	@FXML
	private AnchorPane containerGraph;
	@FXML
	private AnchorPane containerCluster;
	@FXML
	private JFXButton debugPrevious;
	@FXML
	private JFXButton debugNext;
	@FXML
	private JFXButton save;

	private GraphViewer viewerGraph, viewerCluster;

	private GraphProcessingSteps currentStep;

	private ParentController<GraphProcessingSteps> controller;

	@FXML
	public void initialize() {
		// Set graphstream visualizer
		this.viewerGraph = new GraphViewer();
		this.viewerGraph.initialize(GraphView.GRAPH);
		FxViewPanel panel = this.viewerGraph.getViewPanel();
		this.containerGraph.getChildren().setAll(panel);
		LayoutUtils.setAnchorPaneConst(panel);

		// Set graphstream visualizer
		this.viewerCluster = new GraphViewer();
		this.viewerCluster.initialize(GraphView.CLUSTER);
		FxViewPanel panelCluster = this.viewerCluster.getViewPanel();
		this.containerCluster.getChildren().setAll(panelCluster);
		LayoutUtils.setAnchorPaneConst(panelCluster);

		this.debugPrevious.setDisable(true);
		this.debugNext.setDisable(true);
		this.save.setDisable(true);
	}

	@Override
	public void setParentController(ParentController<GraphProcessingSteps> parent) {
		this.controller = parent;
	}

	@Override
	public void reachedProcessStep(GraphProcessingSteps step) {
		this.currentStep = step;
		switch (this.currentStep) {
		case NODE_CREATION:
		case EDGE_CREATION:
		case CALC_WEIGHT:
			this.debugNext.setDisable(false);
			break;
		case SAVE_RESULT:
			this.save.setDisable(false);
		default:
			break;
		}
	}

	@Override
	public <T> void refreshModel(T dto) {
		if (dto instanceof AdjacencyList) {
			if ((this.currentStep != null) && (this.currentStep.equals(GraphProcessingSteps.SOLVE_CLUSTER)
					|| this.currentStep.equals(GraphProcessingSteps.SAVE_RESULT))) {
				this.viewerCluster.reset();
				this.viewerCluster.update((AdjacencyList) dto);
			} else {
				this.viewerGraph.update((AdjacencyList) dto);
			}
		}
	}

	@Override
	public <T> T getModel() {
		return null;
	}

	public void executeDebugPreviousAction() {
		this.controller.approve(this.currentStep, true);
		this.debugPrevious.setDisable(true);
	}

	public void executeDebugNextAction() {
		this.controller.approve(this.currentStep, false);
		this.debugNext.setDisable(true);
	}

	public void executeSaveAction() {
		this.controller.approve(this.currentStep, false);
		this.save.setDisable(true);
	}
}
