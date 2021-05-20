package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;

import data.GenericDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.criteria.CouplingCriteria;
import model.criteria.CouplingGroup;
import model.data.Priorities;
import solver.ClusterAlgorithms;

public class GraphConfigController implements Controller {

	@FXML
	private JFXComboBox<String> algorithmnSelect;
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
	@Override
	public void initialize() throws Exception {
		// Disable logic
		this.numberClusters.setDisable(true);
		this.execute.setDisable(true);
		// Init TabMenu 1
		List<String> items = Arrays.stream(ClusterAlgorithms.values()).map(ClusterAlgorithms::name)
				.collect(Collectors.toList());
		this.algorithmnSelect.getItems().addAll(items);
		this.algorithmnSelect.setPromptText("...");

		// Init TabMenu 2
		List<String> priorities = Arrays.stream(Priorities.values()).map(Priorities::name).collect(Collectors.toList());
		boolean first = true;
		for (CouplingGroup group : CouplingGroup.values()) {
			String key = "graphconfig.config2." + group.name().toLowerCase();
			String value = this.resources.containsKey(key) ? this.resources.getString(key) : group.name();
			String toolTipKey = key + ".tooltip";
			String toolTip = this.resources.containsKey(toolTipKey) ? this.resources.getString(toolTipKey)
					: group.name();
			// Create Header
			Label header = createHeaderLabel(value, toolTip);
			if (first) {
				first = false;
			} else {
				this.priorities.getChildren().add(new Separator());
			}
			this.priorities.getChildren().add(header);
			this.priorities.getChildren().add(new Separator());
			// Create Values of Group
			for (CouplingCriteria criteria : CouplingCriteria.values()) {
				if (!criteria.getGroup().equals(group)) {
					continue;
				}
				String keyCriteria = "graphconfig.config2." + criteria.name().toLowerCase();
				String valueCriteria = this.resources.containsKey(keyCriteria) ? this.resources.getString(keyCriteria)
						: criteria.name();
				String toolTipKeyCriteria = keyCriteria + ".tooltip";
				String toolTipCriteria = this.resources.containsKey(toolTipKeyCriteria)
						? this.resources.getString(toolTipKeyCriteria)
						: criteria.name();
				this.priorities.getChildren()
						.add(createCriteriaSelection(valueCriteria, toolTipCriteria, priorities, priorities.get(0)));
			}
		}

		// Init TabMenu 3
		this.showLabels.setSelected(true);
	}

	private Label createHeaderLabel(String text, String toolTip) {
		Label header = new Label(text);
		header.setContentDisplay(ContentDisplay.RIGHT);
		addTooltipToLabel(header, toolTip);
		return header;
	}

	private VBox createCriteriaSelection(String text, String toolTip, List<String> priorities, String defaultValue) {
		VBox box = new VBox();
		box.getChildren().add(createHeaderLabel(text, toolTip));
		JFXComboBox<String> selection = new JFXComboBox<>();
		selection.getItems().addAll(priorities);
		selection.setValue(defaultValue);
		AnchorPane.setTopAnchor(selection, 0d);
		AnchorPane.setRightAnchor(selection, 0d);
		AnchorPane.setLeftAnchor(selection, 0d);
		AnchorPane.setBottomAnchor(selection, 0d);
		AnchorPane pane = new AnchorPane(selection);
		box.getChildren().add(pane);
		return box;
	}

	private void addTooltipToLabel(Label label, String toolTip) {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("custom-jfx-list-view-icon-container");
		FontIcon icon = new FontIcon();
		icon.setIconLiteral("fas-info-circle");
		icon.getStyleClass().add("custom-jfx-list-view-icon");
		icon.setIconSize(10);
		pane.getChildren().add(icon);
		label.setGraphic(pane);
		Tooltip.install(icon, new Tooltip(toolTip));
	}

	@Override
	public void update(GenericDTO<?> dto) throws Exception {
		// TODO Auto-generated method stub

	}

	public void algorithmnSelectAction() {
		ClusterAlgorithms current = ClusterAlgorithms.valueOf(this.algorithmnSelect.getValue());
		this.numberClusters.setDisable(!current.isDeterministic());
	}
}
