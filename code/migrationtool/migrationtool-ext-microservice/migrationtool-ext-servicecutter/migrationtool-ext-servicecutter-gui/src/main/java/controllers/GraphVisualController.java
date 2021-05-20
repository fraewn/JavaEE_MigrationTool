package controllers;

import org.graphstream.ui.fx_viewer.FxViewPanel;

import data.GenericDTO;
import data.GraphViewer;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import ui.AdjacencyMatrix;

public class GraphVisualController implements Controller {

	private GraphViewer viewer;

	@FXML
	private AnchorPane container;

	@FXML
	@Override
	public void initialize() throws Exception {
		// Set graphstream visualizer
		this.viewer = new GraphViewer();
		this.viewer.initialize();
		FxViewPanel panel = this.viewer.getViewPanel();
		this.container.getChildren().setAll(panel);
		AnchorPane.setTopAnchor(panel, 0d);
		AnchorPane.setBottomAnchor(panel, 0d);
		AnchorPane.setRightAnchor(panel, 0d);
		AnchorPane.setLeftAnchor(panel, 0d);
	}

	@Override
	public void update(GenericDTO<?> dto) {
		if (dto.getObject() instanceof AdjacencyMatrix) {
			this.viewer.update((AdjacencyMatrix) dto.getObject());
		}
	}
}
