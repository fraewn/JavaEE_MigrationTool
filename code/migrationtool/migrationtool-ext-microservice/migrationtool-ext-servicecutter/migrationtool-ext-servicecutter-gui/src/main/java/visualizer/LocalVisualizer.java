package visualizer;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.Appl;
import javafx.application.Application;
import processing.GraphProcessingSteps;
import ui.AdjacencyMatrix;
import ui.Visualizer;

public class LocalVisualizer implements Visualizer {

	private Appl appl;

	public LocalVisualizer() {
		launchAppl();
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
	public void visualizeModel(String jsonString) {
		try {
			this.appl.visualizeModel(jsonString);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	private void launchAppl() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			Application.launch(Appl.class, new String[0]);
		});
		this.appl = Appl.waitForAppl();
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
	public void awaitApproval(GraphProcessingSteps step) {
		if (this.appl != null) {
			while (!Appl.approval) {
				try {
					Thread.sleep(5000);
					Appl.approval = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Appl.approval = false;
		}
	}

}
