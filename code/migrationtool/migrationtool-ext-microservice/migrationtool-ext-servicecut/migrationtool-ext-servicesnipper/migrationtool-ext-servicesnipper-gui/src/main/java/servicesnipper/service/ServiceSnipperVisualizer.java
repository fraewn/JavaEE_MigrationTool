package servicesnipper.service;

import application.Appl;
import application.StartUp;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import service.gui.ResolverConfiguration;
import service.gui.Visualizer;
import servicesnipper.application.ServiceSnipperAppl;
import servicesnipper.model.Model;

public class ServiceSnipperVisualizer implements Visualizer {

	private Appl<GraphProcessingSteps> appl;

	public ServiceSnipperVisualizer() {
		this.appl = new ServiceSnipperAppl();
		StartUp.startUp(this.appl);
	}

	@Override
	public void visualizeModel(String jsonStringBefore, String jsonStringAfter) {
		try {
			this.appl.visualize(new Model(jsonStringBefore, jsonStringAfter), null);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visualizeGraph(AdjacencyList adjList) {
		try {
			this.appl.visualize(Boolean.FALSE, adjList, null);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visualizeCluster(AdjacencyList adjList) {
		try {
			this.appl.visualize(Boolean.TRUE, adjList, null);
		} catch (Exception e) {
			this.appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getEditedModel() {
		return this.appl.getModel();
	}

	@Override
	public ResolverConfiguration getSettings() {
		return this.appl.getModel();
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
