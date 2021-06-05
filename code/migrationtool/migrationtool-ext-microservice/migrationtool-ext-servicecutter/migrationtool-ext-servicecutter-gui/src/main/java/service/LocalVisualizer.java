package service;

import java.util.Map;

import application.Appl;
import graph.clustering.ClusterAlgorithms;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;
import service.gui.Visualizer;

public class LocalVisualizer implements Visualizer {

	private Appl appl;

	public LocalVisualizer() {
		this.appl = new Appl();
		Platform.startup(() -> {
			try {
				this.appl.start(new Stage());
			} catch (Exception e) {
				this.appl.shutdown();
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void visualizeGraph(AdjacencyList adjList) {
		try {
			this.appl.visualizeGraph(adjList);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visualizeCluster(AdjacencyList adjList) {
		try {
			this.appl.visualizeCluster(adjList);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visualizeModel(String jsonString) {
		try {
			this.appl.visualizeModel(jsonString);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setProgress(String label, int progress) {
		try {
			this.appl.setProgress(label, progress);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> getSettings() {
		return this.appl.getSettings();
	}

	@Override
	public ClusterAlgorithms getSelectedAlgorithmn() {
		return this.appl.getSelectedAlgorithmn();
	}

	@Override
	public Map<CouplingCriteria, Priorities> getPriorities() {
		return this.appl.getPriorities();
	}

	@Override
	public boolean awaitApproval(GraphProcessingSteps step) {
		try {
			this.appl.awaitApproval(step);
			while (!this.appl.isApproved(step)) {
				Thread.sleep(500);
			}
			return this.appl.isUndo(step);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void undoStep(GraphProcessingSteps step) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		this.appl.shutdown();
	}
}
