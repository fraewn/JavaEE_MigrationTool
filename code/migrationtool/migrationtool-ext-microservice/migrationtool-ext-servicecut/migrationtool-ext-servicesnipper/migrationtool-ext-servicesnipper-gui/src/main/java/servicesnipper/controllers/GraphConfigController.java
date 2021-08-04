package servicesnipper.controllers;

import static graph.clustering.SolverConfiguration.CHINESE_WHISPERS_NODE_WEIGHT;
import static graph.clustering.SolverConfiguration.LEUNG_PARAM_DELTA;
import static graph.clustering.SolverConfiguration.LEUNG_PARAM_M;
import static graph.clustering.SolverConfiguration.MARKOV_NUMBER_OF_EXPANSIONS;
import static graph.clustering.SolverConfiguration.MARKOV_POWER_COEFFICENT;
import static graph.clustering.SolverConfiguration.NUMBER_CLUSTERS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;

import controllers.ChildController;
import controllers.ParentController;
import graph.clustering.ClusterAlgorithms;
import graph.clustering.NodeWeightings;
import graph.processing.GraphProcessingSteps;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import model.criteria.CouplingCriteria;
import model.criteria.CouplingGroup;
import model.priorities.Priorities;
import service.gui.ResolverConfiguration;
import servicesnipper.model.Configurations;
import utils.ComponentFactory;
import utils.LayoutUtils;

public class GraphConfigController implements ChildController<GraphProcessingSteps> {

	@FXML
	private JFXComboBox<ClusterAlgorithms> algorithmnSelect;
	@FXML
	private VBox priorities;
	@FXML
	private JFXButton execute;
	@FXML
	private JFXToggleButton showLabels;
	@FXML
	private VBox algoConfig;
	@FXML
	private ResourceBundle resources;

	private ParentController<GraphProcessingSteps> controller;

	private GraphProcessingSteps currentStep;

	private Configurations model;

	@FXML
	public void initialize() {
		this.model = new Configurations();
		// Disable logic
		this.execute.setDisable(true);
		// Init TabMenu 1
		List<ClusterAlgorithms> items = Arrays.stream(ClusterAlgorithms.values()).collect(Collectors.toList());
		this.algorithmnSelect.getItems().addAll(items);
		this.algorithmnSelect.setPromptText(this.resources.getString("graphconfig.config1.prompt"));
		this.model.setSelectedAlgorithmn(this.algorithmnSelect.valueProperty());
		// Init TabMenu 2
		initPrios();
		// Init TabMenu 3
		this.showLabels.setSelected(true);
	}

	private void initPrios() {
		List<Priorities> priorities = Arrays.stream(Priorities.values()).collect(Collectors.toList());
		for (CouplingGroup group : CouplingGroup.values()) {
			String key = "graphconfig.config2." + group.name().toLowerCase();
			// Create Header
			Label header = ComponentFactory.createLabelAndToolTip(
					this.resources.getString(key), this.resources.getString(key + ".tooltip"));
			this.priorities.getChildren().add(header);
			this.priorities.getChildren().add(new Separator());
			// Create Values of Group
			for (CouplingCriteria criteria : CouplingCriteria.values()) {
				if (!criteria.getGroup().equals(group)) {
					continue;
				}
				String keyCriteria = "graphconfig.config2." + criteria.name().toLowerCase();
				String valueCriteria = this.resources.getString(keyCriteria);
				String valueToolTipCriteria = this.resources.getString(keyCriteria + ".tooltip");
				JFXComboBox<Priorities> control = ComponentFactory.createComboBox(priorities, group.getDefaultPrio());
				this.model.getBindValues().put(criteria, control.valueProperty());
				VBox box = ComponentFactory.createVerticalForm(valueCriteria, valueToolTipCriteria, control);
				this.priorities.getChildren().add(box);
			}
			this.priorities.getChildren().add(new Separator());
		}
		this.priorities.getChildren().remove(this.priorities.getChildren().size() - 1);
	}

	@Override
	public void setParentController(ParentController<GraphProcessingSteps> parent) {
		this.controller = parent;
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

	@Override
	public <T> void refreshModel(T dto) {
		// Not used
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		ResolverConfiguration config = new ResolverConfiguration();
		config.setSelectedAlgorithmn(this.model.getSelectedAlgorithmn().get());
		Map<CouplingCriteria, Priorities> mapPrio = new HashMap<>();
		for (Entry<CouplingCriteria, ObjectProperty<Priorities>> e : this.model.getBindValues().entrySet()) {
			mapPrio.put(e.getKey(), e.getValue().get());
		}
		config.setPriorities(mapPrio);
		Map<String, String> mapSettings = new HashMap<>();
		for (Entry<String, StringProperty> e : this.model.getAlgorithmnSettings().entrySet()) {
			mapSettings.put(e.getKey(), e.getValue().get());
		}
		config.setSettings(mapSettings);
		return (T) config;
	}

	public void executeSolveAction() {
		this.controller.approve(this.currentStep, this.currentStep.equals(GraphProcessingSteps.SAVE_RESULT));
		this.execute.setDisable(true);
	}

	public void algorithmnSelectAction() {
		ClusterAlgorithms current = this.algorithmnSelect.getValue();
		update(current);
	}

	private void update(ClusterAlgorithms current) {
		this.algoConfig.getChildren().clear();
		this.model.getAlgorithmnSettings().clear();
		String keyPrefix = "graphconfig.config1.";
		switch (current) {
		case GIRVAN_NEWMAN:
			createSettingsGirvanNewman(keyPrefix);
			break;
		case CHINESE_WHISPERS:
			createSettingsChineseWhispers(keyPrefix);
			break;
		case LEUNG:
			createSettingsLeung(keyPrefix);
			break;
		case MARKOV:
			createSettingsMarkov(keyPrefix);
			break;
		default:
			break;
		}
	}

	private void createSettingsGirvanNewman(String keyPrefix) {
		Label labelCluster = ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + NUMBER_CLUSTERS),
				this.resources.getString(keyPrefix + NUMBER_CLUSTERS + ".tooltip"));
		this.algoConfig.getChildren().add(labelCluster);
		JFXSlider slider = ComponentFactory.createSlider(5, 30, 3);
		slider.valueProperty().addListener((obs, oldval, newVal) -> slider.setValue(newVal.intValue()));
		this.algoConfig.getChildren().add(slider);
		StringProperty prop = new SimpleStringProperty();
		IntegerProperty roundedValue = new SimpleIntegerProperty();
		roundedValue.bind(slider.valueProperty());
		Bindings.bindBidirectional(prop, roundedValue, new NumberStringConverter());
		this.model.getAlgorithmnSettings().put(NUMBER_CLUSTERS, prop);
	}

	private void createSettingsChineseWhispers(String keyPrefix) {
		List<NodeWeightings> weights = Arrays.stream(NodeWeightings.values()).collect(Collectors.toList());
		this.algoConfig.getChildren().add(ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + CHINESE_WHISPERS_NODE_WEIGHT),
				this.resources.getString(keyPrefix + CHINESE_WHISPERS_NODE_WEIGHT + ".tooltip")));
		JFXComboBox<NodeWeightings> box = ComponentFactory.createComboBox(weights, NodeWeightings.TOP);
		LayoutUtils.setAnchorPaneConst(box);
		AnchorPane pane = new AnchorPane(box);
		this.algoConfig.getChildren().add(pane);
		StringProperty propBox = new SimpleStringProperty();
		propBox.bind(box.valueProperty().asString());
		this.model.getAlgorithmnSettings().put(CHINESE_WHISPERS_NODE_WEIGHT, propBox);
	}

	private void createSettingsLeung(String keyPrefix) {
		this.algoConfig.getChildren().add(ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + LEUNG_PARAM_M),
				this.resources.getString(keyPrefix + LEUNG_PARAM_M + ".tooltip")));
		JFXSlider sliderM = ComponentFactory.createSlider(0.1, 1, 0.05);
		this.algoConfig.getChildren().add(sliderM);
		StringProperty propM = new SimpleStringProperty();
		Bindings.bindBidirectional(propM, sliderM.valueProperty(), new NumberStringConverter());
		this.model.getAlgorithmnSettings().put(LEUNG_PARAM_M, propM);
		this.algoConfig.getChildren().add(ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + LEUNG_PARAM_DELTA),
				this.resources.getString(keyPrefix + LEUNG_PARAM_DELTA + ".tooltip")));
		JFXSlider sliderDelta = ComponentFactory.createSlider(0.1, 1, 0.1);
		this.algoConfig.getChildren().add(sliderDelta);
		StringProperty propDelta = new SimpleStringProperty();
		Bindings.bindBidirectional(propDelta, sliderDelta.valueProperty(), new NumberStringConverter());
		this.model.getAlgorithmnSettings().put(LEUNG_PARAM_DELTA, propDelta);
	}

	private void createSettingsMarkov(String keyPrefix) {
		this.algoConfig.getChildren().add(ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + MARKOV_NUMBER_OF_EXPANSIONS),
				this.resources.getString(keyPrefix + MARKOV_NUMBER_OF_EXPANSIONS + ".tooltip")));
		JFXSlider sliderExpansion = ComponentFactory.createSlider(1, 10, 2);
		this.algoConfig.getChildren().add(sliderExpansion);
		StringProperty propExpansion = new SimpleStringProperty();
		Bindings.bindBidirectional(propExpansion, sliderExpansion.valueProperty(), new NumberStringConverter());
		this.model.getAlgorithmnSettings().put(MARKOV_NUMBER_OF_EXPANSIONS, propExpansion);
		this.algoConfig.getChildren().add(ComponentFactory.createLabelAndToolTip(
				this.resources.getString(keyPrefix + MARKOV_POWER_COEFFICENT),
				this.resources.getString(keyPrefix + MARKOV_POWER_COEFFICENT + ".tooltip")));
		JFXSlider sliderPower = ComponentFactory.createSlider(1, 10, 2);
		this.algoConfig.getChildren().add(sliderPower);
		StringProperty propPower = new SimpleStringProperty();
		Bindings.bindBidirectional(propPower, sliderPower.valueProperty(), new NumberStringConverter());
		this.model.getAlgorithmnSettings().put(MARKOV_POWER_COEFFICENT, propPower);
	}
}
