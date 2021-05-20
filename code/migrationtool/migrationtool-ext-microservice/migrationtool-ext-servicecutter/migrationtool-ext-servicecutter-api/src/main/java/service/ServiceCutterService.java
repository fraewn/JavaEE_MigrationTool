package service;

import model.ModelRepresentation;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import solver.Solver;
import solver.SolverConfiguration;
import ui.AdjacencyMatrix;

public interface ServiceCutterService {

	void importArchitecture(ModelRepresentation rep);

	void process(GraphProcessingSteps currentStep, ProcessAutomate<?> subProcess);

	AdjacencyMatrix getCurrentGraphState();

	Solver getSolver(SolverConfiguration config);
}
