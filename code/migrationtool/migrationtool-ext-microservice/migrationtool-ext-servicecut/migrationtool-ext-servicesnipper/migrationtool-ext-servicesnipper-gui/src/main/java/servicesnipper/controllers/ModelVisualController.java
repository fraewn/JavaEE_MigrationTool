package servicesnipper.controllers;

import java.util.ResourceBundle;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextArea;

import controllers.ChildController;
import controllers.ParentController;
import graph.processing.GraphProcessingSteps;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.util.Duration;
import model.ModelRepresentation;
import servicesnipper.model.Model;
import utils.JsonConverter;

public class ModelVisualController implements ChildController<GraphProcessingSteps> {

	@FXML
	private JFXTextArea currentArea;

	@FXML
	private JFXTextArea previousArea;

	@FXML
	private JFXButton discard;

	@FXML
	private JFXButton save;

	@FXML
	private JFXCheckBox appliedChange;

	@FXML
	private Label detailsText;

	@FXML
	private ResourceBundle resources;

	private ParentController<GraphProcessingSteps> controller;

	@FXML
	public void initialize() {
		this.previousArea.setEditable(false);
		this.appliedChange.setDisable(true);
		this.appliedChange.setSelected(true);

		PauseTransition pause = new PauseTransition(Duration.seconds(5));
		this.currentArea.textProperty().addListener((obs, oldText, newText) -> {
			this.appliedChange.setSelected(false);
			if (pause.getCurrentTime().greaterThan(Duration.seconds(0))) {
				pause.playFromStart();
			}
			pause.setOnFinished(event -> {
				try {
					JsonConverter.getMapper().readValue(this.currentArea.getText(), ModelRepresentation.class);
					this.appliedChange.setSelected(true);
					this.controller.callbackEvent(this);
				} catch (Exception e) {
					Platform.runLater(() -> {
						e.printStackTrace();
						JFXAlert<Void> alert = new JFXAlert<>(this.save.getScene().getWindow());
						JFXDialogLayout layout = new JFXDialogLayout();
						layout.setBody(new Label("Error: "));
						alert.setOverlayClose(true);
						alert.setAnimation(JFXAlertAnimation.SMOOTH);
						alert.setContent(layout);
						alert.initModality(Modality.NONE);
						alert.showAndWait();
					});
				}
			});
			pause.play();
		});
	}

	@Override
	public void setParentController(ParentController<GraphProcessingSteps> parent) {
		this.controller = parent;
	}

	@Override
	public void reachedProcessStep(GraphProcessingSteps step) {
		// NOT NEEDED
	}

	@Override
	public <T> void refreshModel(T dto) {
		if (dto instanceof Model) {
			Model model = (Model) dto;
			if (!model.getJsonStringBefore().equals(this.previousArea.getText())) {
				this.previousArea.setText(model.getJsonStringBefore());
			}
			if (!model.getJsonStringAfter().equals(this.currentArea.getText())) {
				this.currentArea.setText(model.getJsonStringAfter());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		Model model = new Model();
		model.setJsonStringBefore(this.previousArea.getText());
		model.setJsonStringAfter(this.currentArea.getText());
		return (T) model;
	}

	public void executeDiscardAction() {
		this.controller.approve(GraphProcessingSteps.EDIT_MODEL, true);
	}

	public void executeSaveAction() {
		try {
			JsonConverter.getMapper().readValue(this.currentArea.getText(), ModelRepresentation.class);
			this.controller.approve(GraphProcessingSteps.EDIT_MODEL, false);
		} catch (Exception e) {
			JFXAlert<Void> alert = new JFXAlert<>(this.save.getScene().getWindow());
			JFXDialogLayout layout = new JFXDialogLayout();
			layout.setBody(new Label("Error: " + e.getMessage()));
			alert.setOverlayClose(true);
			alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
			alert.setContent(layout);
			alert.initModality(Modality.NONE);
			alert.showAndWait();
		}
	}
}
