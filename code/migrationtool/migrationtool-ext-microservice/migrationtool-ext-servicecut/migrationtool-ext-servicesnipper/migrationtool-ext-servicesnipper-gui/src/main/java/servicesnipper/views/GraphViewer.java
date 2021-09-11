package servicesnipper.views;

import static org.graphstream.ui.view.util.InteractiveElement.EDGE;
import static org.graphstream.ui.view.util.InteractiveElement.NODE;
import static org.graphstream.ui.view.util.InteractiveElement.SPRITE;

import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.camera.Camera;

import graph.model.GraphModel;
import javafx.scene.input.MouseEvent;

public class GraphViewer {

	private static final DecimalFormat df = new DecimalFormat("#.##");

	private Graph graph;
	private GraphView view;
	private FxViewPanel panel;

	private double zoomLevel = 1.0;
	private MouseEvent lastDragged;

	private static Map<String, Float> contextColor = new HashMap<>();

	public void initialize(GraphView view) {
		this.view = view;
		System.setProperty("org.graphstream.ui", "javafx");
		System.setProperty("org.graphstream.debug", "true");

		this.graph = new SingleGraph("ServiceIsolator");
		reset();

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

	public void update(GraphModel model) {
		if (this.graph.getNodeCount() == 0) {
			for (String node : model.getGraph().keySet()) {
				this.graph.addNode(node);
				Node n = this.graph.getNode(node);
				n.setAttribute("ui.label", node);
				n.setAttribute("layout.weight", 5);
				if (node.matches("[0-9]+-[A-z]+")) {
					n.setAttribute("ui.class", "service");
				}
				if (node.contains(".")) {
					String context = node.substring(0, node.lastIndexOf("."));
					if (!contextColor.containsKey(context)) {
						contextColor.put(context, (float) Math.random());
					}
					n.setAttribute("ui.color", GraphViewer.contextColor.get(context));
				}
			}
		}
		for (Entry<String, Map<String, Double>> node : model.getGraph().entrySet()) {
			for (Entry<String, Double> edge : node.getValue().entrySet()) {
				String id = node.getKey() + edge.getKey();
				if (this.graph.getEdge(id) == null) {
					this.graph.addEdge(id, node.getKey(), edge.getKey());
					this.graph.getEdge(id).setAttribute("layout.weight", 5);
				}
				if (edge.getValue() != 0) {
					this.graph.getEdge(id).setAttribute("ui.label", "" + df.format(edge.getValue()));
				}
			}
		}
	}

	public void reset() {
		this.graph.clear();
		this.graph.setAttribute("ui.stylesheet", "url('file://" + this.view.getCssFile() + "')");
		this.graph.setAttribute("layout.quality", 1);
	}

	public void showLabels(boolean visible) {
		for (Node node : this.graph) {
			node.setAttribute("ui.style", "text-visibility: 0.3;");
			node.setAttribute("ui.style", "text-visibility-mode: " + (visible ? "normal;" : "under-zoom;"));
		}
		for (int i = 0; i < this.graph.getEdgeCount(); i++) {
			Edge e = this.graph.getEdge(i);
			e.setAttribute("ui.style", "text-visibility: 0.3;");
			e.setAttribute("ui.style", "text-visibility-mode: " + (visible ? "normal;" : "under-zoom;"));
		}
	}

	public void newColors() {
		for (Entry<String, Float> e : contextColor.entrySet()) {
			contextColor.put(e.getKey(), (float) Math.random());
		}
		for (Node node : this.graph) {
			String text = (String) node.getAttribute("ui.label");
			String context = text.substring(0, text.lastIndexOf("."));
			node.setAttribute("ui.color", GraphViewer.contextColor.get(context));
		}
	}

	public FxViewPanel getViewPanel() {
		return this.panel;
	}
}
