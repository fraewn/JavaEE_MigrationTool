package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;

import graph.clustering.ClusterAlgorithms;
import graph.processing.GraphProcessingSteps;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import model.criteria.CouplingCriteria;
import model.criteria.CouplingGroup;
import model.priorities.Priorities;
import models.Configurations;
import utils.ComponentFactory;
import visualization.ChildController;
import visualization.ParentController;

public class GraphConfigController implements ChildController {

	private GraphProcessingSteps currentStep;

	private Configurations model;

	private ParentController controller;

	@FXML
	private JFXComboBox<ClusterAlgorithms> algorithmnSelect;
	@FXML
	private JFXSlider numberClusters;
	@FXML
	private VBox priorities;
	@FXML
	private JFXButton execute;
	@FXML
	private JFXToggleButton showLabels;
	@FXML
	private ResourceBundle resources;

	@FXML
	public void initialize() {
		this.model = new Configurations();
		// Disable logic
		this.numberClusters.setDisable(true);
		this.execute.setDisable(true);
		// Init TabMenu 1
		List<ClusterAlgorithms> items = Arrays.stream(ClusterAlgorithms.values()).collect(Collectors.toList());
		this.algorithmnSelect.getItems().addAll(items);
		this.algorithmnSelect.setPromptText(this.resources.getString("graphconfig.config1.prompt"));
		this.model.setSelectedAlgorithmn(this.algorithmnSelect.valueProperty());
		// Init TabMenu 2
		List<Priorities> priorities = Arrays.stream(Priorities.values()).collect(Collectors.toList());
		for (CouplingGroup group : CouplingGroup.values()) {
			String key = "graphconfig.config2." + group.name().toLowerCase();
			String keyToolTip = key + ".tooltip";
			String value = this.resources.getString(key);
			String valueToolTip = this.resources.getString(keyToolTip);
			// Create Header
			Label header = ComponentFactory.createLabelAndToolTip(value, valueToolTip);
			this.priorities.getChildren().add(header);
			this.priorities.getChildren().add(new Separator());
			// Create Values of Group
			for (CouplingCriteria criteria : CouplingCriteria.values()) {
				if (!criteria.getGroup().equals(group)) {
					continue;
				}
				String keyCriteria = "graphconfig.config2." + criteria.name().toLowerCase();
				String keyToolTipCriteria = keyCriteria + ".tooltip";
				String valueCriteria = this.resources.getString(keyCriteria);
				String valueToolTipCriteria = this.resources.getString(keyToolTipCriteria);
				JFXComboBox<Priorities> control = ComponentFactory.createComboBox(priorities, Priorities.IGNORE);
				this.model.getBindValues().put(criteria, control.valueProperty());
				control.valueProperty().setValue(group.getDefaultPrio());
				VBox box = ComponentFactory.createVerticalForm(valueCriteria, valueToolTipCriteria, control);
				this.priorities.getChildren().add(box);
			}
			this.priorities.getChildren().add(new Separator());
		}
		this.priorities.getChildren().remove(this.priorities.getChildren().size() - 1);

		// Init TabMenu 3
		this.showLabels.setSelected(true);
	}

	public void algorithmnSelectAction() {
		ClusterAlgorithms current = this.algorithmnSelect.getValue();
		this.numberClusters.setDisable(!current.isDeterministic());
	}

	@Override
	public void setParentController(ParentController parent) {
		this.controller = parent;
	}

	@Override
	public <T> void refreshModel(T dto) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		return (T) this.model;
	}

	@Override
	public void reachedProcessStep(GraphProcessingSteps step) {
		this.currentStep = step;
		switch (this.currentStep) {
		case SOLVE_CLUSTER:
		case SAVE_RESULT:
			this.execute.setDisable(false);
		default:
			break;
		}
	}

	public void executeSolveAction() {
		this.controller.approve(this.currentStep, this.currentStep.equals(GraphProcessingSteps.SAVE_RESULT));
		this.execute.setDisable(true);
	}
}
