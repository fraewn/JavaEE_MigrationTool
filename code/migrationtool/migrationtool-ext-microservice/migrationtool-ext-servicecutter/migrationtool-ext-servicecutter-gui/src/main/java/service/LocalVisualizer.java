package service;

import java.util.Map;

import application.Appl;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.criteria.CouplingCriteria;
import model.data.Priorities;
import processing.GraphProcessingSteps;
import solver.ClusterAlgorithms;
import ui.AdjacencyMatrix;
import ui.Visualizer;

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
	public void visualizeGraph(AdjacencyMatrix matrix) {
		try {
			this.appl.visualizeGraph(matrix);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visualizeCluster(AdjacencyMatrix matrix) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClusterAlgorithms getSelectedAlgorithmn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CouplingCriteria, Priorities> getPriorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void awaitApproval(GraphProcessingSteps step) {
		try {
			this.appl.awaitApproval(step);
			while (!this.appl.isApproved(step)) {
				Thread.sleep(500);
			}
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void undoStep(GraphProcessingSteps step) {
		// TODO Auto-generated method stub

	}
}
