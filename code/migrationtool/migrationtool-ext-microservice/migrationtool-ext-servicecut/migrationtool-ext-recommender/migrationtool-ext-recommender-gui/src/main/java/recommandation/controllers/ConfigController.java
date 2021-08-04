package recommandation.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import controllers.ChildController;
import controllers.ParentController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import recommendaton.model.Configuration;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import utils.ComponentFactory;
import utils.LayoutUtils;

public class ConfigController implements ChildController<RecommenderProcessingSteps> {

	@FXML
	private Label criteria;

	@FXML
	private Label description;

	@FXML
	private JFXTreeTableView<RecommandObject> recommandationTable;

	@FXML
	private StackPane stackPane;

	@FXML
	private JFXButton addGroup;

	@FXML
	private ResourceBundle resources;

	private ParentController<RecommenderProcessingSteps> controller;

	private RecommenderProcessingSteps currentStep;

	private JFXDialog dialog;

	private ObservableList<RecommandObject> data;

	@FXML
	public void initialize() {
		this.description.setWrapText(true);
		this.addGroup.setText(this.resources.getString("config.addGroup"));
		// Dialog
		JFXDialogLayout content = new JFXDialogLayout();
		this.dialog = new JFXDialog(this.stackPane, content, JFXDialog.DialogTransition.CENTER);
	}

	@Override
	public void setParentController(ParentController<RecommenderProcessingSteps> parent) {
		this.controller = parent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void beforeRefreshModel(T dto) {
		String desc = this.resources.getString(this.currentStep.name() + ".description");
		List<Integer> limits = (List<Integer>) dto;
		List<String> names = this.currentStep.getGroups();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < limits.size(); i++) {
			sb.append(names.get(i) + " => x > " + limits.get(i) + "; ");
		}
		this.description.setText(desc + "\n" + sb.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void refreshModel(T dto) {
		Map<String, Recommendation> list = (Map<String, Recommendation>) dto;
		loadTable(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModel() {
		List<Recommendation> result = new ArrayList<>();
		for (RecommandObject recommandObject : this.data) {
			boolean unique = true;
			for (Recommendation recommandation : result) {
				if (recommandation.getName().equals(recommandObject.name.get())) {
					unique = false;
					break;
				}
			}
			if (unique) {
				Recommendation rec = new Recommendation(recommandObject.name.get(), recommandObject.metricValue.get(),
						recommandObject.relatedGroups.get(), recommandObject.included.get());
				result.add(rec);
			}
		}
		Configuration conf = new Configuration();
		conf.getRecommendations().addAll(result);
		return (T) conf;
	}

	@Override
	public void reachedProcessStep(RecommenderProcessingSteps step) {
		this.currentStep = step;
		this.criteria.setText(this.resources.getString(step.name()));
	}

	private void loadTable(Map<String, Recommendation> list) {
		List<JFXTreeTableColumn<RecommandObject, ?>> columns = new ArrayList<>();
		columns.add(setupColumn(new JFXTreeTableColumn<>(this.resources.getString("config.table.columnGroup")), 0.4,
				RecommandObject::relatedGroupsProperty));
		columns.add(setupColumn(new JFXTreeTableColumn<>(this.resources.getString("config.table.columnMetric")), 0.09,
				RecommandObject::metricValueProperty));
		columns.add(setupColumn(new JFXTreeTableColumn<>(this.resources.getString("config.table.columnName")), 0.4,
				RecommandObject::nameProperty));
		JFXTreeTableColumn<RecommandObject, Boolean> actions = new JFXTreeTableColumn<>(
				this.resources.getString("config.table.columnIncluded"));
		Callback<TreeTableColumn<RecommandObject, Boolean>, TreeTableCell<RecommandObject, Boolean>> cellFactory = param -> {
			TreeTableCell<RecommandObject, Boolean> cell = new TreeTableCell<>() {
				private JFXCheckBox box = new JFXCheckBox();

				@Override
				public void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);
					setText(null);
					if (item != null) {
						this.box.setSelected(item);
						this.box.setOnAction(event -> {
							getTreeTableRow().getItem().included.set(this.box.isSelected());
							updateResult();
						});

					}
					setGraphic(empty || (item == null) ? null : this.box);
				}
			};
			cell.setStyle("-fx-alignment: CENTER;");
			return cell;
		};
		actions.setCellFactory(cellFactory);
		columns.add(setupColumn(actions, 0.1, RecommandObject::includedProperty));
		this.data = FXCollections.observableArrayList();
		if (list != null) {
			for (Entry<String, Recommendation> recommandObject : list.entrySet()) {
				Recommendation rec = recommandObject.getValue();
				String group = rec.getRelatedGroup();
				this.data.add(new RecommandObject(rec.getName(), rec.getMetricValue(), rec.isIncluded(), group));
			}
		}

		this.recommandationTable.setRoot(new RecursiveTreeItem<>(this.data, RecursiveTreeObject::getChildren));
		this.recommandationTable.setShowRoot(false);
		this.recommandationTable.getColumns().addAll(columns);
	}

	private void updateResult() {
		this.controller.callbackEvent();
	}

	private <T> JFXTreeTableColumn<RecommandObject, T> setupColumn(JFXTreeTableColumn<RecommandObject, T> column,
			double width, Function<RecommandObject, ObservableValue<T>> mapper) {
		column.prefWidthProperty().bind(this.recommandationTable.widthProperty().multiply(width));
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<RecommandObject, T> param) -> {
			if (column.validateValue(param)) {
				return mapper.apply(param.getValue().getValue());
			}
			return column.getComputedValue(param);
		});
		return column;
	}

	public void createModelForm() {
		JFXDialogLayout content = new JFXDialogLayout();
		content.setHeading(new Text(this.resources.getString("dialog.header")));
		GridPane pane = ComponentFactory.createForm();
		Label labelName = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.name"),
				this.resources.getString("dialog.name.tooltip"));
		JFXTextField name = ComponentFactory.createTextField();
		LayoutUtils.createRequiredValidator(name, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelName, 0, 0);
		LayoutUtils.setGridPaneConst(name, 0, 1);
		pane.getChildren().addAll(labelName, name);
		Label labelRelatedGroup = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.group"),
				this.resources.getString("dialog.group.tooltip"));
		List<String> autoSearch = new ArrayList<>();
		for (RecommandObject rec : this.data) {
			if (!autoSearch.contains(rec.relatedGroups.get())) {
				autoSearch.add(rec.relatedGroups.get());
			}
		}
		JFXTextField relatedGroup = ComponentFactory.createAutoSearchTextField(autoSearch);
		LayoutUtils.createRequiredValidator(relatedGroup, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelRelatedGroup, 1, 0);
		LayoutUtils.setGridPaneConst(relatedGroup, 1, 1);
		pane.getChildren().addAll(labelRelatedGroup, relatedGroup);
		Label labelMetric = ComponentFactory.createLabelAndToolTip(this.resources.getString("dialog.metric"),
				this.resources.getString("dialog.metric.tooltip"));
		JFXTextField metric = ComponentFactory.createNumericField();
		LayoutUtils.createRequiredValidator(metric, this.resources.getString("error.requiredMessage"));
		LayoutUtils.setGridPaneConst(labelMetric, 2, 0);
		LayoutUtils.setGridPaneConst(metric, 2, 1);
		pane.getChildren().addAll(labelMetric, metric);
		content.setBody(pane);
		JFXButton buttonClose = new JFXButton(this.resources.getString("dialog.discard"));
		JFXButton buttonCreate = new JFXButton(this.resources.getString("dialog.save"));
		buttonClose.setOnAction(event -> {
			this.dialog.close();
		});
		buttonCreate.setOnAction(event -> {
			try {
				int z = Integer.parseInt(metric.getText());
				if ((name.getText() != null) && !name.getText().isEmpty() && (relatedGroup.getText() != null)
						&& !relatedGroup.getText().isEmpty()) {
					RecommandObject rec = new RecommandObject(name.getText(), z, true, relatedGroup.getText());
					this.data.add(rec);
					this.controller.callbackEvent();
					this.dialog.close();
				} else {
					name.validate();
					relatedGroup.validate();
				}
			} catch (NumberFormatException e) {
				metric.validate();
				name.validate();
				relatedGroup.validate();
			}
		});
		content.setActions(buttonClose, buttonCreate);
		this.dialog.setContent(content);
		this.dialog.show();
	}

	private static final class RecommandObject extends RecursiveTreeObject<RecommandObject> {
		final StringProperty name;
		final IntegerProperty metricValue;
		final BooleanProperty included;
		final StringProperty relatedGroups;

		public RecommandObject(String name, Integer metricValue, Boolean included, String relatedGroups) {
			this.name = new SimpleStringProperty(name);
			this.metricValue = new SimpleIntegerProperty(metricValue);
			this.included = new SimpleBooleanProperty(included);
			this.relatedGroups = new SimpleStringProperty(relatedGroups);
		}

		StringProperty nameProperty() {
			return this.name;
		}

		IntegerProperty metricValueProperty() {
			return this.metricValue;
		}

		BooleanProperty includedProperty() {
			return this.included;
		}

		StringProperty relatedGroupsProperty() {
			return this.relatedGroups;
		}
	}
}
