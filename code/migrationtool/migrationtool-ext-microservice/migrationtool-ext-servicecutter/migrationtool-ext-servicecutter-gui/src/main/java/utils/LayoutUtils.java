package utils;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class LayoutUtils {

	public static void setAnchorPaneConst(Node child) {
		AnchorPane.setTopAnchor(child, 0d);
		AnchorPane.setRightAnchor(child, 0d);
		AnchorPane.setLeftAnchor(child, 0d);
		AnchorPane.setBottomAnchor(child, 0d);
	}

	public static void setGridPaneConst(Node child, int rowIndex, int columnIndex) {
		setGridPaneConst(child, rowIndex, columnIndex, 1, 1);
	}

	public static void setGridPaneConst(Node child, int rowIndex, int columnIndex, int rowSpan, int columnSpan) {
		GridPane.setRowIndex(child, rowIndex);
		GridPane.setColumnIndex(child, columnIndex);
		GridPane.setColumnSpan(child, columnSpan);
		GridPane.setRowSpan(child, rowSpan);
	}

	public static void addTooltip(Label label, String toolTip) {
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

	public static void createRequiredValidator(JFXTextField control, String msg) {
		control.setValidators(new RequiredFieldValidator(msg));
		control.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				control.validate();
			}
		});
	}
}
