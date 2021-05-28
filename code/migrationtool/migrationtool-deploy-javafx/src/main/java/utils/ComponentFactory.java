package utils;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ComponentFactory {

	public static Label createLabel(String text) {
		Label header = new Label(text);
		header.setContentDisplay(ContentDisplay.LEFT);
		return header;
	}

	public static Label createLabelAndToolTip(String text, String toolTip) {
		Label header = createLabel(text);
		header.setPadding(new Insets(0, 0, 0, 5));
		LayoutUtils.addTooltip(header, toolTip);
		return header;
	}

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
}
