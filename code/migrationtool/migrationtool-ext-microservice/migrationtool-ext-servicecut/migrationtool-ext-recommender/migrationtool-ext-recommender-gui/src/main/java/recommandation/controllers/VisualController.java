package recommandation.controllers;

import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import controllers.ChildController;
import controllers.ParentController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.data.ArchitectureInformation;
import recommender.processing.RecommenderProcessingSteps;
import utils.JsonConverter;

public class VisualController implements ChildController<RecommenderProcessingSteps> {

	@FXML
	private Label resultHeader;

	@FXML
	private JFXButton skip;

	@FXML
	private JFXButton save;

	@FXML
	private JFXTextArea result;

	@FXML
	private ResourceBundle resources;

	private RecommenderProcessingSteps currentStep;

	private ParentController<RecommenderProcessingSteps> controller;

	@FXML
	public void initialize() {
		this.result.setEditable(false);
		this.skip.setText(this.resources.getString("visual.skip"));
		this.save.setText(this.resources.getString("visual.apply"));
	}

	public void executeApplyAction() {
		this.controller.approve(this.currentStep, false);
	}

	public void executeDiscardAction() {
		this.controller.approve(this.currentStep, true);
	}

	@Override
	public void setParentController(ParentController<RecommenderProcessingSteps> parent) {
		this.controller = parent;
	}

	@Override
	public <T> void refreshModel(T dto) {
		if (dto instanceof ArchitectureInformation) {
			ArchitectureInformation ai = (ArchitectureInformation) dto;
			String json = JsonConverter.toJsonString(ai);
			this.result.setText(json);
		}
	}

	@Override
	public void reachedProcessStep(RecommenderProcessingSteps step) {
		this.currentStep = step;
		this.resultHeader.setText(step.name() + " " + this.resources.getString("visual.result"));
	}

	@Override
	public <T> T getModel() {
		return null;
	}
}
