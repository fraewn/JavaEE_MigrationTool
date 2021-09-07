package servicesnipper.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.graphstream.ui.fx_viewer.FxViewPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import controllers.ChildController;
import controllers.ParentController;
import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.Result;
import model.data.Instance;
import model.data.UseCase;
import model.serviceDefintion.Service;
import model.serviceDefintion.ServiceRelation;
import servicesnipper.views.GraphView;
import servicesnipper.views.GraphViewer;
import utils.ComponentFactory;
import utils.LayoutUtils;

public class GraphVisualController implements ChildController<GraphProcessingSteps> {

	private static final DecimalFormat df = new DecimalFormat("#.##");
	@FXML
	private ResourceBundle resources;
	@FXML
	private AnchorPane containerGraph;
	@FXML
	private AnchorPane containerCluster;
	@FXML
	private AnchorPane debugText;
	@FXML
	private HBox debugOptions;
	@FXML
	private JFXButton debugNext;
	@FXML
	private JFXButton save;
	@FXML
	private JFXToggleButton showLabels;
	@FXML
	private JFXTreeTableView<EntityObject> entityTable;

	private ObservableList<EntityObject> entityData;
	@FXML
	private JFXTreeTableView<EdgeObject> edgeTable;

	private ObservableList<EdgeObject> edgeData;
	@FXML
	private JFXTreeTableView<ServiceObject> serviceEntityTable;

	private ObservableList<ServiceObject> serviceEntityData;
	@FXML
	private JFXTreeTableView<ServiceObject> serviceUseCaseTable;

	private ObservableList<ServiceObject> serviceUsecaseData;
	@FXML
	private JFXTreeTableView<ServiceRelationObject> serviceRelationTable;

	private ObservableList<ServiceRelationObject> serviceRelationData;

	private GraphViewer viewerGraph, viewerCluster;

	private GraphProcessingSteps currentStep;

	private Result currentResult;

	private ParentController<GraphProcessingSteps> controller;

	@FXML
	public void initialize() {
		this.showLabels.setSelected(true);
		this.showLabels.setOnAction(event -> {
			this.viewerGraph.showLabels(this.showLabels.isSelected());
			this.viewerCluster.showLabels(this.showLabels.isSelected());
		});
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

		this.debugNext.setDisable(true);
		this.save.setDisable(true);

		setupTableEntity();
		setupTableEdges();
		setupTableServiceEntity();
		setupTableServiceUseCase();
		setupTableServiceRelation();
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
		case SOLVE_CLUSTER:
			this.debugText.setVisible(false);
			this.debugOptions.setVisible(false);
			break;
		case SAVE_RESULT:
			this.save.setDisable(false);
		default:
			break;
		}
	}

	@Override
	public <T> void beforeRefreshModel(T dto) {
		if (dto instanceof Result) {
			if ((this.currentStep != null) && (this.currentStep.equals(GraphProcessingSteps.SOLVE_CLUSTER)
					|| this.currentStep.equals(GraphProcessingSteps.SAVE_RESULT))) {
				this.currentResult = (Result) dto;
			}
		}
	}

	@Override
	public <T> void refreshModel(T dto) {
		if (dto instanceof GraphModel) {
			if ((this.currentStep != null) && (this.currentStep.equals(GraphProcessingSteps.SOLVE_CLUSTER)
					|| this.currentStep.equals(GraphProcessingSteps.SAVE_RESULT))) {
				this.viewerCluster.reset();
				reloadTableCluster((GraphModel) dto);
				this.viewerCluster.update((GraphModel) dto);
			} else {
				reloadTableGraph((GraphModel) dto);
				this.viewerGraph.update((GraphModel) dto);
			}
		}
	}

	@Override
	public <T> T getModel() {
		return null;
	}

	public void executeDebugNextAction() {
		this.controller.approve(this.currentStep, false);
		this.debugNext.setDisable(true);
	}

	public void executeSaveAction() {
		this.controller.approve(this.currentStep, false);
		this.save.setDisable(true);
	}

	public void executeRecalcColors() {
		this.viewerGraph.newColors();
	}

	private void setupTableEntity() {
		List<JFXTreeTableColumn<EntityObject, ?>> columns = new ArrayList<>();
		columns.add(ComponentFactory.setupColumn(this.entityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityContext")), 0.5,
				EntityObject::contextProperty));
		columns.add(ComponentFactory.setupColumn(this.entityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityName")), 0.5,
				EntityObject::nameProperty));
		this.entityData = FXCollections.observableArrayList();

		this.entityTable.setRoot(new RecursiveTreeItem<>(this.entityData, RecursiveTreeObject::getChildren));
		this.entityTable.setShowRoot(false);
		this.entityTable.getColumns().addAll(columns);
	}

	private void setupTableEdges() {
		List<JFXTreeTableColumn<EdgeObject, ?>> columns = new ArrayList<>();
		columns.add(ComponentFactory.setupColumn(this.edgeTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityA")), 0.4,
				EdgeObject::vertexAProperty));
		columns.add(ComponentFactory.setupColumn(this.edgeTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityB")), 0.4,
				EdgeObject::vertexBProperty));
		columns.add(ComponentFactory.setupColumn(this.edgeTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEdgeWeight")), 0.2,
				EdgeObject::weightProperty));
		this.edgeData = FXCollections.observableArrayList();

		this.edgeTable.setRoot(new RecursiveTreeItem<>(this.edgeData, RecursiveTreeObject::getChildren));
		this.edgeTable.setShowRoot(false);
		this.edgeTable.getColumns().addAll(columns);
	}

	private void setupTableServiceEntity() {
		List<JFXTreeTableColumn<ServiceObject, ?>> columns = new ArrayList<>();
		columns.add(ComponentFactory.setupColumn(this.serviceEntityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnServiceName")), 0.5,
				ServiceObject::contextProperty));
		columns.add(ComponentFactory.setupColumn(this.serviceEntityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnInstanceName")), 0.5,
				ServiceObject::nameProperty));
		this.serviceEntityData = FXCollections.observableArrayList();

		this.serviceEntityTable
				.setRoot(new RecursiveTreeItem<>(this.serviceEntityData, RecursiveTreeObject::getChildren));
		this.serviceEntityTable.setShowRoot(false);
		this.serviceEntityTable.getColumns().addAll(columns);
	}

	private void setupTableServiceUseCase() {
		List<JFXTreeTableColumn<ServiceObject, ?>> columns = new ArrayList<>();
		columns.add(ComponentFactory.setupColumn(this.serviceEntityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnServiceName")), 0.5,
				ServiceObject::contextProperty));
		columns.add(ComponentFactory.setupColumn(this.serviceEntityTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnUseCaseName")), 0.5,
				ServiceObject::nameProperty));
		this.serviceUsecaseData = FXCollections.observableArrayList();

		this.serviceUseCaseTable
				.setRoot(new RecursiveTreeItem<>(this.serviceUsecaseData, RecursiveTreeObject::getChildren));
		this.serviceUseCaseTable.setShowRoot(false);
		this.serviceUseCaseTable.getColumns().addAll(columns);
	}

	private void setupTableServiceRelation() {
		List<JFXTreeTableColumn<ServiceRelationObject, ?>> columns = new ArrayList<>();
		columns.add(ComponentFactory.setupColumn(this.serviceRelationTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityA")), 0.25,
				ServiceRelationObject::serviceAProperty));
		columns.add(ComponentFactory.setupColumn(this.serviceRelationTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.columnEntityB")), 0.25,
				ServiceRelationObject::serviceBProperty));
		columns.add(ComponentFactory.setupColumn(this.serviceRelationTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.serviceRelationDirection")), 0.15,
				ServiceRelationObject::directionProperty));
		columns.add(ComponentFactory.setupColumn(this.serviceRelationTable,
				new JFXTreeTableColumn<>(this.resources.getString("graphvisual.table.serviceRelationSharedEntities")),
				0.35,
				ServiceRelationObject::sharedProperty));
		this.serviceRelationData = FXCollections.observableArrayList();

		this.serviceRelationTable
				.setRoot(new RecursiveTreeItem<>(this.serviceRelationData, RecursiveTreeObject::getChildren));
		this.serviceRelationTable.setShowRoot(false);
		this.serviceRelationTable.getColumns().addAll(columns);
	}

	private void reloadTableGraph(GraphModel adjList) {
		if (adjList != null) {
			this.entityData.clear();
			this.edgeData.clear();
			for (Entry<String, Map<String, Double>> entry : adjList.getGraph().entrySet()) {
				int z = entry.getKey().lastIndexOf(".");
				String context = entry.getKey().substring(0, z);
				String name = entry.getKey().substring(z + 1);
				this.entityData.add(new EntityObject(context, name));
				if ((entry.getValue() != null) && !entry.getValue().isEmpty()) {
					for (Entry<String, Double> edge : entry.getValue().entrySet()) {
						this.edgeData.add(new EdgeObject(entry.getKey(), edge.getKey(),
								Double.parseDouble(df.format(edge.getValue()).replace(",", "."))));
					}
				}
			}
		}
	}

	private void reloadTableCluster(GraphModel adjList) {
		if ((adjList != null) && (this.currentResult != null)) {
			this.serviceEntityData.clear();
			for (Service service : this.currentResult.getIsolatedServices().getServices()) {
				for (Instance instance : service.getInstances()) {
					this.serviceEntityData.add(new ServiceObject(service.getName(), instance.getQualifiedName()));
				}
			}
			this.serviceUsecaseData.clear();
			for (Entry<Service, List<UseCase>> entry : this.currentResult.getIsolatedServices().getRelatedUseCases()
					.entrySet()) {
				for (UseCase useCase : entry.getValue()) {
					this.serviceUsecaseData.add(new ServiceObject(entry.getKey().getName(), useCase.getName()));
				}
			}
			this.serviceRelationData.clear();
			for (ServiceRelation relation : this.currentResult.getIsolatedServices().getRelations()) {
				String shared = relation.getSharedEntities().stream().map(Instance::getQualifiedName)
						.collect(Collectors.joining(","));
				this.serviceRelationData.add(new ServiceRelationObject(relation.getServiceIdA(),
						relation.getServiceIdB(), relation.getDirection().toString(), shared));
			}
		}
	}

	private static final class EntityObject extends RecursiveTreeObject<EntityObject> {
		final StringProperty context;
		final StringProperty name;

		public EntityObject(String context, String name) {
			this.name = new SimpleStringProperty(name);
			this.context = new SimpleStringProperty(context);
		}

		StringProperty contextProperty() {
			return this.context;
		}

		StringProperty nameProperty() {
			return this.name;
		}
	}

	private static final class EdgeObject extends RecursiveTreeObject<EdgeObject> {
		final StringProperty vertexA;
		final StringProperty vertexB;
		final DoubleProperty weight;

		public EdgeObject(String vertexA, String vertexB, double weight) {
			this.vertexA = new SimpleStringProperty(vertexA);
			this.vertexB = new SimpleStringProperty(vertexB);
			this.weight = new SimpleDoubleProperty(weight);
		}

		StringProperty vertexAProperty() {
			return this.vertexA;
		}

		StringProperty vertexBProperty() {
			return this.vertexB;
		}

		DoubleProperty weightProperty() {
			return this.weight;
		}
	}

	private static final class ServiceObject extends RecursiveTreeObject<ServiceObject> {
		final StringProperty context;
		final StringProperty name;

		public ServiceObject(String context, String name) {
			this.name = new SimpleStringProperty(name);
			this.context = new SimpleStringProperty(context);
		}

		StringProperty contextProperty() {
			return this.context;
		}

		StringProperty nameProperty() {
			return this.name;
		}
	}

	private static final class ServiceRelationObject extends RecursiveTreeObject<ServiceRelationObject> {
		final StringProperty serviceA;
		final StringProperty serviceB;
		final StringProperty direction;
		final StringProperty shared;

		public ServiceRelationObject(String serviceA, String serviceB, String direction, String shared) {
			this.serviceA = new SimpleStringProperty(serviceA);
			this.serviceB = new SimpleStringProperty(serviceB);
			this.direction = new SimpleStringProperty(direction);
			this.shared = new SimpleStringProperty(shared);
		}

		StringProperty serviceAProperty() {
			return this.serviceA;
		}

		StringProperty serviceBProperty() {
			return this.serviceB;
		}

		StringProperty directionProperty() {
			return this.direction;
		}

		StringProperty sharedProperty() {
			return this.shared;
		}
	}
}
