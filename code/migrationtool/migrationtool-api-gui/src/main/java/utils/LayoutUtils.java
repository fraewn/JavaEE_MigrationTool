package utils;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Factory class for the most used layouts in the GUI
 */
public class LayoutUtils {

	/**
	 * Sets all consts of an anchorpane to 0
	 *
	 * @param child the component
	 */
	public static void setAnchorPaneConst(Node child) {
		AnchorPane.setTopAnchor(child, 0d);
		AnchorPane.setRightAnchor(child, 0d);
		AnchorPane.setLeftAnchor(child, 0d);
		AnchorPane.setBottomAnchor(child, 0d);
	}

	/**
	 * sets the consts for a grid with size 1
	 *
	 * @param child       the component
	 * @param rowIndex    index of row
	 * @param columnIndex index of column
	 */
	public static void setGridPaneConst(Node child, int rowIndex, int columnIndex) {
		setGridPaneConst(child, rowIndex, columnIndex, 1, 1);
	}

	/**
	 * sets the consts for a grid
	 *
	 * @param child       the component
	 * @param rowIndex    index of row
	 * @param columnIndex index of column
	 * @param rowSpan     size of row
	 * @param columnSpan  size of column
	 */
	public static void setGridPaneConst(Node child, int rowIndex, int columnIndex, int rowSpan, int columnSpan) {
		GridPane.setRowIndex(child, rowIndex);
		GridPane.setColumnIndex(child, columnIndex);
		GridPane.setColumnSpan(child, columnSpan);
		GridPane.setRowSpan(child, rowSpan);
	}

	/**
	 * Creates a required validator for the specified control
	 *
	 * @param control the component
	 * @param msg     required message
	 */
	public static void createRequiredValidator(JFXTextField control, String msg) {
		control.setValidators(new RequiredFieldValidator(msg));
		control.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				control.validate();
			}
		});
	}

	/**
	 * Creates a required validator for the specified control
	 *
	 * @param control the component
	 * @param msg     required message
	 */
	public static <T> void createRequiredValidator(JFXComboBox<T> control, String msg) {
		control.setValidators(new RequiredFieldValidator(msg));
		control.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				control.validate();
			}
		});
	}
}
