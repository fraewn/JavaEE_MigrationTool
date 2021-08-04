package servicesnipper.controllers;

import static model.criteria.CouplingGroup.COMPATIBILITY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import controllers.ChildController;
import controllers.ParentController;
import graph.processing.GraphProcessingSteps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import model.ModelRepresentation;
import model.artifacts.ArchitectureArtifact;
import model.criteria.CompatibitlyCharacteristics;
import model.criteria.CompatibitlyMapper;
import model.criteria.CouplingCriteria;
import model.data.Characteristic;
import model.data.ContextGroup;
import model.data.Instance;
import model.data.UseCase;
import model.erm.Entity;
import model.erm.EntityRelation;
import model.erm.RelationType;
import servicesnipper.model.Model;
import utils.ComponentFactory;
import utils.JsonConverter;
import utils.LayoutUtils;

public class ModelConfigController implements ChildController<GraphProcessingSteps> {

	@FXML
	private JFXListView<String> dialogPane;

	@FXML
	private ResourceBundle resources;

	private ParentController<GraphProcessingSteps> controller;

	private JFXDialog dialog;

	private Map<String, Runnable> mapping;

	private ObservableList<String> observableList;

	private ModelRepresentation currentModel;

	public ModelConfigController() {
		this.mapping = new LinkedHashMap<>();
	}

	@FXML
	public void initialize() {
		this.mapping.put(this.resources.getString("model.menu.entity"), this::createModelFormEntity);
		this.mapping.put(this.resources.getString("model.menu.relationship"), this::createModelFormRelationShip);
		this.mapping.put(this.resources.getString("model.menu.useCase"), this::createModelFormUseCase);
		this.mapping.put(this.resources.getString("model.menu.criteria"), this::createModelFormCriteria);
		this.mapping.put(this.resources.getString("model.menu.compatibility"), this::createModelFormCompatibility);

		this.observableList = FXCollections.observableArrayList();
		this.observableList.addAll(this.mapping.keySet());
		this.dialogPane.setItems(this.observableList);
		this.dialogPane.setCellFactory(listView -> new CustomCell());
	}

	@Override
	public <T> void afterInitialization(T dto) {
		if (dto instanceof StackPane) {
			// Dialog
			JFXDialogLayout content = new JFXDialogLayout();
			this.dialog = new JFXDialog((StackPane) dto, content, JFXDialog.DialogTransition.CENTER);
		}
	}

	@Override
	public void setParentController(ParentController<GraphProcessingSteps> parent) {
		this.controller = parent;
	}

	@Override
	public void reachedProcessStep(GraphProcessingSteps step) {
		// Not needed
	}

	@Override
	public <T> void refreshModel(T dto) {
		if (dto instanceof Model) {
			Model model = (Model) dto;
			this.currentModel = JsonConverter.readJson(model.getJsonStringAfter(), ModelRepresentation.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		return (T) this.currentModel;
	}

	class CustomCell extends JFXListCell<String> {
		private HBox container = new HBox();
		private Label text = new Label();
		private JFXButton box = new JFXButton();

		public CustomCell() {
			this.box.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			FontIcon icon = new FontIcon(FontAwesomeSolid.PLUS);
			this.box.setGraphic(icon);
			this.container.setPadding(new Insets(0, 5, 0, 5));
			Pane pane = new Pane();
			HBox.setHgrow(pane, Priority.ALWAYS);
			this.box.setContentDisplay(ContentDisplay.TOP);
			this.box.setOnAction(event -> {
				if (!this.text.getText().isBlank()) {
					Runnable run = ModelConfigController.this.mapping.get(this.text.getText());
					if (run != null) {
						run.run();
					}
				}
			});
			LayoutUtils.setAnchorPaneConst(this.text);
			AnchorPane holder = new AnchorPane(this.text);
			this.container.getChildren().addAll(holder, pane, this.box);
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);
			setGraphic(null);
			if ((item != null) && !empty) {
				this.text.setText(item);
				setGraphic(this.container);
			}
		}
	}

	private List<String> allEntities() {
		List<String> res = new ArrayList<>();
		if (this.currentModel != null) {
			for (Entity entity : this.currentModel.getEntityDiagram().getEntities()) {
				res.add(entity.getName());
			}
		}
		return res;
	}

	private Entity getEntityFromName(String name) {
		if (this.currentModel != null) {
			for (Entity entity : this.currentModel.getEntityDiagram().getEntities()) {
				if (entity.getName().equals(name)) {
					return entity;
				}
			}
		}
		return null;
	}

	private List<String> allInstances() {
		List<String> res = new ArrayList<>();
		if (this.currentModel != null) {
			for (Entity entity : this.currentModel.getEntityDiagram().getEntities()) {
				for (String attr : entity.getAttributes()) {
					res.add(entity.getName() + "." + attr);
				}
			}
		}
		return res;
	}

	private JFXDialogLayout createGenericDialog(String header) {
		JFXDialogLayout content = new JFXDialogLayout();
		content.setHeading(new Text(header));
		JFXButton buttonClose = new JFXButton(this.resources.getString("dialog.discard"));
		JFXButton buttonCreate = new JFXButton(this.resources.getString("dialog.save"));
		buttonClose.setOnAction(event -> {
			this.dialog.close();
		});
		content.setActions(buttonClose, buttonCreate);
		this.dialog.setContent(content);
		return content;
	}

	private void createGenericAddListView(String label, ObservableList<String> observableList, GridPane pane, int row,
			int sizeListView) {
		Label labelInstance = ComponentFactory.createLabelAndToolTip(this.resources.getString(label),
				this.resources.getString(label + ".tooltip"));
		JFXTextField instance = ComponentFactory.createAutoSearchTextField(allInstances());
		JFXButton add = new JFXButton();
		add.setContentDisplay(ContentDisplay.TOP);
		add.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		add.setGraphic(new FontIcon(FontAwesomeSolid.PLUS));
		add.setOnAction(event -> {
			if (!instance.getText().isBlank() && !observableList.contains(instance.getText())) {
				observableList.add(instance.getText());
			}
		});
		HBox container = new HBox(instance, add);
		HBox.setHgrow(instance, Priority.ALWAYS);
		LayoutUtils.setGridPaneConst(labelInstance, row, 0);
		LayoutUtils.setGridPaneConst(container, row, 1);
		pane.getChildren().addAll(labelInstance, container);

		JFXListView<String> listInstances = new JFXListView<>();
		listInstances.setPrefHeight(150);
		listInstances.setItems(observableList);
		LayoutUtils.setAnchorPaneConst(listInstances);
		AnchorPane child = new AnchorPane(listInstances);
		ScrollPane scrolling = new ScrollPane(child);
		scrolling.setFitToWidth(true);
		scrolling.setFitToHeight(true);
		LayoutUtils.setGridPaneConst(scrolling, row + 1, 0, sizeListView, 2);
		pane.getChildren().addAll(scrolling);
	}

	private void createModelFormEntity() {
		JFXDialogLayout content = createGenericDialog(this.resources.getString("dialog.header.entity"));
		GridPane pane = ComponentFactory.createForm();
		content.setBody(pane);

		Label labelName = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.entity"),
				this.resources.getString("dialog.entity.tooltip"));
		JFXTextField name = ComponentFactory.createTextField();
		LayoutUtils.createRequiredValidator(name, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelName, 0, 0);
		LayoutUtils.setGridPaneConst(name, 0, 1);
		pane.getChildren().addAll(labelName, name);

		ObservableList<String> observableList = FXCollections.observableArrayList();
		createGenericAddListView("dialog.instances", observableList, pane, 1, 2);

		JFXButton create = (JFXButton) content.getActions().get(1);
		create.setOnAction(event -> {
			if (!name.getText().isBlank() && !observableList.isEmpty()) {
				Entity e = new Entity();
				e.setName(name.getText());
				e.getAttributes().addAll(observableList);
				this.currentModel.getEntityDiagram().getEntities().add(e);
				this.dialog.close();
				this.controller.callbackEvent(this);
			}
		});

		this.dialog.show();
	}

	private void createModelFormRelationShip() {
		JFXDialogLayout content = createGenericDialog(this.resources.getString("dialog.header.relationship"));
		GridPane pane = ComponentFactory.createForm();
		content.setBody(pane);

		Label labelNameA = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.entity"),
				this.resources.getString("dialog.entity.tooltip"));
		JFXComboBox<String> entityA = ComponentFactory.createComboBox(allEntities(), null);
		LayoutUtils.createRequiredValidator(entityA, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelNameA, 0, 0);
		LayoutUtils.setAnchorPaneConst(entityA);
		AnchorPane holder1 = new AnchorPane(entityA);
		LayoutUtils.setGridPaneConst(holder1, 0, 1);
		pane.getChildren().addAll(labelNameA, holder1);

		Label labelNameB = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.entity"),
				this.resources.getString("dialog.entity.tooltip"));
		JFXComboBox<String> entityB = ComponentFactory.createComboBox(allEntities(), null);
		LayoutUtils.createRequiredValidator(entityB, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelNameB, 1, 0);
		LayoutUtils.setAnchorPaneConst(entityB);
		AnchorPane holder2 = new AnchorPane(entityB);
		LayoutUtils.setGridPaneConst(holder2, 1, 1);
		pane.getChildren().addAll(labelNameB, holder2);

		Label labelRelationship = ComponentFactory.createLabelAndToolTip(
				this.resources.getString("dialog.relationship"),
				this.resources.getString("dialog.relationship.tooltip"));
		List<RelationType> types = Arrays.asList(RelationType.values());
		JFXComboBox<RelationType> relationship = ComponentFactory.createComboBox(types, null);
		LayoutUtils.createRequiredValidator(relationship, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelRelationship, 2, 0);
		LayoutUtils.setAnchorPaneConst(relationship);
		AnchorPane holder3 = new AnchorPane(relationship);
		LayoutUtils.setGridPaneConst(holder3, 2, 1);
		pane.getChildren().addAll(labelRelationship, holder3);

		JFXButton create = (JFXButton) content.getActions().get(1);
		create.setOnAction(event -> {
			if ((entityA.getValue() != null) && (entityB.getValue() != null) && (relationship.getValue() != null)) {
				EntityRelation e = new EntityRelation();
				e.setOrigin(getEntityFromName(entityA.getValue()));
				e.setDestination(getEntityFromName(entityB.getValue()));
				e.setType(relationship.getValue());
				this.currentModel.getEntityDiagram().getRelations().add(e);
				this.dialog.close();
				this.controller.callbackEvent(this);
			}
		});

		this.dialog.show();
	}

	private void createModelFormUseCase() {
		JFXDialogLayout content = createGenericDialog(this.resources.getString("dialog.header.useCase"));
		GridPane pane = ComponentFactory.createForm();
		content.setBody(pane);

		Label labelName = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.useCase"),
				this.resources.getString("dialog.useCase.tooltip"));
		JFXTextField name = ComponentFactory.createTextField();
		LayoutUtils.createRequiredValidator(name, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelName, 0, 0);
		LayoutUtils.setGridPaneConst(name, 0, 1);
		pane.getChildren().addAll(labelName, name);

		Label labelLatency = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.latency"),
				this.resources.getString("dialog.latency.tooltip"));
		JFXCheckBox latency = new JFXCheckBox();
		LayoutUtils.setGridPaneConst(labelLatency, 1, 0);
		LayoutUtils.setGridPaneConst(latency, 1, 1);
		pane.getChildren().addAll(labelLatency, latency);

		ObservableList<String> observableListRead = FXCollections.observableArrayList();
		createGenericAddListView("dialog.readInput", observableListRead, pane, 2, 1);

		ObservableList<String> observableListWrite = FXCollections.observableArrayList();
		createGenericAddListView("dialog.writeInput", observableListWrite, pane, 4, 1);

		JFXButton create = (JFXButton) content.getActions().get(1);
		create.setOnAction(event -> {
			if (!name.getText().isBlank() && !observableListRead.isEmpty() && !observableListWrite.isEmpty()) {
				UseCase u = new UseCase();
				u.setName(name.getText());
				u.setLatencyCritical(latency.isSelected());
				u.getInput().addAll(observableListRead.stream().map(Instance::new).collect(Collectors.toList()));
				u.getPersistenceChanges()
						.addAll(observableListWrite.stream().map(Instance::new).collect(Collectors.toList()));
				this.currentModel.getInformation().getUseCases().add(u);
				this.dialog.close();
				this.controller.callbackEvent(this);
			}
		});
		this.dialog.show();
	}

	private void createModelFormCriteria() {
		JFXDialogLayout content = createGenericDialog(this.resources.getString("dialog.header.criteria"));
		GridPane pane = ComponentFactory.createForm();
		content.setBody(pane);

		Label labelCriteria = ComponentFactory.createLabelAndToolTip(
				this.resources.getString("dialog.criteria"),
				this.resources.getString("dialog.criteria.tooltip"));
		List<ArchitectureArtifact> types = new ArrayList<>(Arrays.asList(ArchitectureArtifact.values()));
		types.remove(ArchitectureArtifact.ENTITIES);
		types.remove(ArchitectureArtifact.USE_CASE);
		types.remove(ArchitectureArtifact.RELATIONSHIPS);
		types.remove(ArchitectureArtifact.COMPATIBILITIES);
		JFXComboBox<ArchitectureArtifact> criteria = ComponentFactory.createComboBox(types, null);
		LayoutUtils.createRequiredValidator(criteria, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelCriteria, 0, 0);
		LayoutUtils.setAnchorPaneConst(criteria);
		AnchorPane holder = new AnchorPane(criteria);
		LayoutUtils.setGridPaneConst(holder, 0, 1);
		pane.getChildren().addAll(labelCriteria, holder);

		Label labelName = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.criteria.name"),
				this.resources.getString("dialog.criteria.name.tooltip"));
		JFXTextField name = ComponentFactory.createTextField();
		LayoutUtils.createRequiredValidator(name, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelName, 1, 0);
		LayoutUtils.setGridPaneConst(name, 1, 1);
		pane.getChildren().addAll(labelName, name);

		ObservableList<String> observableList = FXCollections.observableArrayList();
		createGenericAddListView("dialog.instances", observableList, pane, 2, 2);

		JFXButton create = (JFXButton) content.getActions().get(1);
		create.setOnAction(event -> {
			if (!name.getText().isBlank() && !observableList.isEmpty() && (criteria.getValue() != null)) {
				ContextGroup group = new ContextGroup();
				group.setName(name.getText());
				group.getInstances().addAll(observableList.stream().map(Instance::new).collect(Collectors.toList()));
				List<ContextGroup> list = this.currentModel.getInformation().getCriteria().get(criteria.getValue());
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(group);
				this.currentModel.getInformation().getCriteria().put(criteria.getValue(), list);
				this.dialog.close();
				this.controller.callbackEvent(this);
			}
		});

		this.dialog.show();
	}

	private void createModelFormCompatibility() {
		JFXDialogLayout content = createGenericDialog(this.resources.getString("dialog.header.compatibility"));
		GridPane pane = ComponentFactory.createForm();
		content.setBody(pane);

		Label labelCriteria = ComponentFactory.createLabelAndToolTip(
				this.resources.getString("dialog.criteria"),
				this.resources.getString("dialog.criteria.tooltip"));
		List<CouplingCriteria> types = Arrays.asList(CouplingCriteria.values());
		types = types.stream().filter(x -> x.getGroup().equals(COMPATIBILITY)).collect(Collectors.toList());
		JFXComboBox<CouplingCriteria> criteria = ComponentFactory.createComboBox(types, null);
		LayoutUtils.createRequiredValidator(criteria, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelCriteria, 0, 0);
		LayoutUtils.setAnchorPaneConst(criteria);
		AnchorPane holder = new AnchorPane(criteria);
		LayoutUtils.setGridPaneConst(holder, 0, 1);
		pane.getChildren().addAll(labelCriteria, holder);

		Label labelName = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.criteria.name"),
				this.resources.getString("dialog.criteria.name.tooltip"));
		JFXTextField name = ComponentFactory.createTextField();
		LayoutUtils.createRequiredValidator(name, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelName, 1, 0);
		LayoutUtils.setGridPaneConst(name, 1, 1);
		pane.getChildren().addAll(labelName, name);

		Label labelCharacteristic = ComponentFactory.createLabelAndToolTip(
				this.resources.getString("dialog.characteristic"),
				this.resources.getString("dialog.characteristic.tooltip"));
		List<CompatibitlyCharacteristics> characteristicList = Arrays.asList(CompatibitlyCharacteristics.values());
		JFXComboBox<CompatibitlyCharacteristics> characteristic = ComponentFactory.createComboBox(characteristicList,
				null);
		LayoutUtils.createRequiredValidator(characteristic, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelCharacteristic, 2, 0);
		LayoutUtils.setAnchorPaneConst(characteristic);
		AnchorPane holder2 = new AnchorPane(characteristic);
		LayoutUtils.setGridPaneConst(holder2, 2, 1);
		pane.getChildren().addAll(labelCharacteristic, holder2);

		criteria.setOnAction(event -> {
			if (criteria.getValue() != null) {
				CompatibitlyMapper mapper = CompatibitlyMapper.getMapperFromCriteria(criteria.getValue());
				ObservableList<CompatibitlyCharacteristics> list = FXCollections.observableArrayList();
				list.addAll(mapper.getCharacterisitics());
				characteristic.setItems(list);
			}
		});

		ObservableList<String> observableList = FXCollections.observableArrayList();
		createGenericAddListView("dialog.instances", observableList, pane, 3, 2);

		JFXButton create = (JFXButton) content.getActions().get(1);
		create.setOnAction(event -> {
			if (!name.getText().isBlank() && !observableList.isEmpty() && (criteria.getValue() != null)
					&& (characteristic != null)) {
				Characteristic group = new Characteristic();
				group.setName(name.getText());
				group.setCharacteristic(characteristic.getValue());
				group.getInstances().addAll(observableList.stream().map(Instance::new).collect(Collectors.toList()));
				List<Characteristic> list = this.currentModel.getInformation().getCompatibilities()
						.get(criteria.getValue());
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(group);
				this.currentModel.getInformation().getCompatibilities().put(criteria.getValue(), list);
				this.dialog.close();
				this.controller.callbackEvent(this);
			}
		});

		this.dialog.show();
	}
}
