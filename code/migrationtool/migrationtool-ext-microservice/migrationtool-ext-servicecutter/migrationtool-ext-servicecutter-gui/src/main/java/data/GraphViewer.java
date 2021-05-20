package data;

import static org.graphstream.ui.view.util.InteractiveElement.EDGE;
import static org.graphstream.ui.view.util.InteractiveElement.NODE;
import static org.graphstream.ui.view.util.InteractiveElement.SPRITE;

import java.util.EnumSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.camera.Camera;

import javafx.scene.input.MouseEvent;
import ui.AdjacencyMatrix;

public class GraphViewer {

	private Graph graph;
	private FxViewPanel panel;

	private double zoomLevel = 1.0;
	private MouseEvent lastDragged;

	private int counter = 1;
	private int lexicalCounter = 0;

	public void initialize() throws Exception {
		System.setProperty("org.graphstream.ui", "javafx");
		System.setProperty("org.graphstream.debug", "true");

		this.graph = new SingleGraph("ServiceIsolator");
//		this.graph.setAttribute("ui.stylesheet", "url('file://graphstream/visual_settings.css')");
		this.graph.setAttribute("layout.quality", 4);

		FxViewer viewer = new FxViewer(this.graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		viewer.enableAutoLayout();
		this.panel = (FxViewPanel) viewer.addView(FxViewer.DEFAULT_VIEW_ID, new FxGraphRenderer());
		initMouseListeners();
	}

	private void initMouseListeners() {
		this.panel.setOnScroll(event -> {
			// Adjust the zoom factor as per your requirement
			double deltaY = event.getDeltaY();
			if (deltaY > 0) {
				this.zoomLevel = this.zoomLevel - 0.05;
				if (this.zoomLevel < 0.1) {
					this.zoomLevel = 0.1;
				}
			}
			if (deltaY < 0) {
				this.zoomLevel = this.zoomLevel + 0.05;
			}
			this.panel.getCamera().setViewPercent(this.zoomLevel);
		});
		this.panel.setOnMouseDragged(event -> {
			GraphicElement e = this.panel.findGraphicElementAt(EnumSet.of(EDGE, NODE, SPRITE), event.getX(),
					event.getY());
			if (e != null) {
				// No movement on elements
				return;
			}
			if (this.lastDragged != null) {
				Camera camera = this.panel.getCamera();
				// see DefaultShortcutManager
				Point3 p1 = camera.getViewCenter();
				Point3 p2 = camera.transformGuToPx(p1.x, p1.y, 0);
				int xdelta = (int) (event.getX() - this.lastDragged.getX());
				int ydelta = (int) (event.getY() - this.lastDragged.getY());
				p2.x -= xdelta;
				p2.y -= ydelta;
				Point3 p3 = camera.transformPxToGu(p2.x, p2.y);
				camera.setViewCenter(p3.x, p3.y, 0);
				event.consume();
			}
			this.lastDragged = event;
		});
		this.panel.setOnMousePressed(event -> {
			this.lastDragged = null;
		});
	}

	public void update(AdjacencyMatrix matrix) {
		if (this.graph.getNodeCount() != matrix.getLabelNodes().size()) {
			for (String node : matrix.getLabelNodes()) {
				String id = uniqueId();
				this.graph.addNode(id);
				this.graph.getNode(id).setAttribute("ui.label", node);
			}
		}
		for (int i = 0; i < matrix.getMatrix().length; i++) {
			for (int j = 0; j < matrix.getMatrix()[i].length; j++) {
				double value = matrix.getMatrix()[i][j];
				if (value >= 0) {
					String origin = this.graph.getNode(i).getId();
					String destination = this.graph.getNode(j).getId();
					if (this.graph.getEdge(origin + destination) == null) {
						this.graph.addEdge(origin + destination, origin, destination);
					}
				}
			}
		}
	}

	public FxViewPanel getViewPanel() {
		return this.panel;
	}

	private String uniqueId() {
		String unique = "" + this.counter + "-" + ((char) (this.lexicalCounter + 67));
		this.lexicalCounter++;
		if (this.lexicalCounter >= 26) {
			this.counter++;
			this.lexicalCounter = 0;
		}
		return unique;
	}
}
