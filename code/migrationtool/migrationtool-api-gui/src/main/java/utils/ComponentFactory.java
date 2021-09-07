package utils;

import java.util.List;
import java.util.function.Function;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.IntegerValidator;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Factory class for the most used components in the GUI
 */
public class ComponentFactory {

	/**
	 * Creates a simple label
	 *
	 * @param text test of label
	 * @return label
	 */
	public static Label createLabel(String text) {
		Label header = new Label(text);
		header.setContentDisplay(ContentDisplay.LEFT);
		return header;
	}

	/**
	 * Creates a simple label with a tooltip
	 *
	 * @param text test of label
	 * @return label
	 */
	public static Label createLabelAndToolTip(String text, String toolTip) {
		Label header = createLabel(text);
		header.setPadding(new Insets(0, 0, 0, 5));
		createTooltip(header, toolTip);
		return header;
	}

	/**
	 * Create a simple tooltip for a label
	 *
	 * @param label   label
	 * @param toolTip tooltip text
	 */
	public static void createTooltip(Label label, String toolTip) {
		StackPane pane = new StackPane();
		FontIcon icon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
		icon.setIconSize(10);
		pane.getChildren().add(icon);
		label.setGraphic(pane);
		Tooltip tip = new Tooltip(toolTip);
		tip.setShowDuration(Duration.seconds(5));
		tip.setMaxWidth(350);
		tip.setWrapText(true);
		Tooltip.install(icon, tip);
	}

	/**
	 * Creates a simple numeric text field, only integers are allowed
	 *
	 * @return textfield
	 */
	public static JFXTextField createNumericField() {
		JFXTextField field = new JFXTextField();
		field.getValidators().add(new IntegerValidator());
		field.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				field.validate();
			}
		});
		return field;
	}

	/**
	 * Creates a simple textfield
	 *
	 * @return textfield
	 */
	public static JFXTextField createTextField() {
		JFXTextField field = new JFXTextField();
		return field;
	}

	/**
	 * Create a simple textfield with a suggestion list
	 *
	 * @param suggestions list of suggestions
	 * @return textfield
	 */
	public static JFXTextField createAutoSearchTextField(List<String> suggestions) {
		JFXTextField field = new JFXTextField();
		JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
		autoCompletePopup.getSuggestions().addAll(suggestions);
		autoCompletePopup.setSelectionHandler(event -> {
			field.setText(event.getObject());
		});
		field.textProperty().addListener(observable -> {
			autoCompletePopup.filter(item -> item.toLowerCase().contains(field.getText().toLowerCase()));
			if (autoCompletePopup.getFilteredSuggestions().isEmpty()) {
				autoCompletePopup.hide();
			} else {
				autoCompletePopup.show(field);
			}
		});
		return field;
	}

	/**
	 * Creates a simple horizontal form
	 *
	 * @return gridPane
	 */
	public static GridPane createForm() {
		GridPane form = new GridPane();
		form.setHgap(30);
		form.setVgap(25);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.NEVER);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setHgrow(Priority.ALWAYS);
		form.getColumnConstraints().addAll(col1, col2);
		return form;
	}

	/**
	 * Creates a simple Combobox
	 *
	 * @param <T>          the object
	 * @param priorities   items
	 * @param defaultValue initial value
	 * @return combobox
	 */
	public static <T> JFXComboBox<T> createComboBox(List<T> priorities, T defaultValue) {
		JFXComboBox<T> selection = new JFXComboBox<>();
		selection.getItems().addAll(priorities);
		if (defaultValue != null) {
			selection.setValue(defaultValue);
		}
		return selection;
	}

	/**
	 * Creates a simple slider
	 *
	 * @param tickUnit scala
	 * @param max      max value
	 * @param current  current value
	 * @return slider
	 */
	public static JFXSlider createSlider(double tickUnit, double max, double current) {
		JFXSlider slider = new JFXSlider();
		slider.setMajorTickUnit(tickUnit);
		slider.setMax(max);
		slider.setValue(current);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		return slider;
	}

	/**
	 * Create a simple vertical form
	 *
	 * @param text    text of label
	 * @param toolTip tooltip of label
	 * @param node    interaction component
	 * @return form
	 */
	public static VBox createVerticalForm(String text, String toolTip, Control node) {
		Label label = createLabelAndToolTip(text, toolTip);
		LayoutUtils.setAnchorPaneConst(node);
		AnchorPane pane = new AnchorPane(node);
		return new VBox(label, pane);
	}

	public static <I extends RecursiveTreeObject<I>, T> JFXTreeTableColumn<I, T> setupColumn(JFXTreeTableView<I> table,
			JFXTreeTableColumn<I, T> column, double width, Function<I, ObservableValue<T>> mapper) {
		column.prefWidthProperty().bind(table.widthProperty().multiply(width));
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<I, T> param) -> {
			if (column.validateValue(param)) {
				return mapper.apply(param.getValue().getValue());
			}
			return column.getComputedValue(param);
		});
		return column;
	}
}
