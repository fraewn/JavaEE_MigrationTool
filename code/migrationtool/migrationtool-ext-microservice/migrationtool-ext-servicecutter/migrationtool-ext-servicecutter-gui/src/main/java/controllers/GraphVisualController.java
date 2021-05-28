package controllers;

import org.graphstream.ui.fx_viewer.FxViewPanel;

import com.jfoenix.controls.JFXButton;

import data.GraphViewer;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import processing.GraphProcessingSteps;
import ui.AdjacencyMatrix;
import utils.LayoutUtils;
import visualization.ChildController;
import visualization.ParentController;

public class GraphVisualController implements ChildController<AdjacencyMatrix> {

	private GraphViewer viewerGraph;

	private GraphViewer viewerCluster;

	private GraphProcessingSteps currentStep;

	private ParentController controller;

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

	@FXML
	public void initialize() {
		// Set graphstream visualizer
		this.viewerGraph = new GraphViewer();
		this.viewerGraph.initialize();
		FxViewPanel panel = this.viewerGraph.getViewPanel();
		this.containerGraph.getChildren().setAll(panel);
		LayoutUtils.setAnchorPaneConst(panel);

		// Set graphstream visualizer
		this.viewerCluster = new GraphViewer();
		this.viewerCluster.initialize();
		FxViewPanel panelCluster = this.viewerCluster.getViewPanel();
		this.containerCluster.getChildren().setAll(panelCluster);
		LayoutUtils.setAnchorPaneConst(panelCluster);

		this.debugPrevious.setDisable(true);
		this.debugNext.setDisable(true);
		this.save.setDisable(true);
	}

	@Override
	public void setParentController(ParentController parent) {
		this.controller = parent;
	}

	@Override
	public void refreshContent(AdjacencyMatrix dto) {
		this.viewerGraph.update(dto);
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
		case FINISHED:
			this.save.setDisable(false);
		default:
			break;
		}
	}

	public void executeDebugPreviousAction() {
		this.controller.approve(this.currentStep);
	}

	public void executeDebugNextAction() {
		this.controller.approve(this.currentStep);
		this.debugNext.setDisable(true);
	}

	public void executeSaveAction() {

	}

}
