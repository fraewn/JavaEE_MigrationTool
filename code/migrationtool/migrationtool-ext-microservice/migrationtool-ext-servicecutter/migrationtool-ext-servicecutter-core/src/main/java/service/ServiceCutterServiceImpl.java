package service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import core.CouplingGroup;
import core.Edge;
import core.Graph;
import core.Node;
import model.ModelRepresentation;
import model.data.Instance;
import model.erm.Entity;
import model.erm.EntityRelationDiagram;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import resolver.CriteriaScorer;
import solver.Solver;
import solver.SolverConfiguration;
import ui.AdjacencyMatrix;

public class ServiceCutterServiceImpl implements ServiceCutterService {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(ServiceCutterServiceImpl.class);

	private Graph graph;

	private ModelRepresentation rep;

	public ServiceCutterServiceImpl() {
		this.graph = new Graph();
	}

	@Override
	public void importArchitecture(ModelRepresentation rep) {
		this.graph = new Graph();
		this.rep = rep;
		EntityRelationDiagram er = rep.getEntityDiagram();
		this.graph.getNodes().addAll(convertToNodes(er));
	}

	@Override
	public void process(GraphProcessingSteps currentStep, ProcessAutomate<?> subProcess) {
		switch (currentStep) {
		case EDGE_CREATION:
			edgeCreation(subProcess);
			break;
		case CALC_WEIGHT:
			edgeResolving(subProcess);
			break;
		default:
			break;
		}
	}

	private void edgeCreation(ProcessAutomate<?> subProcess) {
		LOG.info("Creating edges of type: " + subProcess.name());
		List<CouplingGroup> relatedGroups = this.graph.getRelatedGroups();
		ProcessSteps processing = ProcessSteps.getStep(subProcess);
		StepInformation info = processing.execute(this.rep);
		for (CouplingGroup group : info.getGroups()) {
			LOG.info("Group created " + group.getGroupName() + " has " + group.getRelatedEdges().size() + " edges");
			relatedGroups.add(group);
			for (Edge edge : group.getRelatedEdges()) {
				this.graph.addNewEdge(edge);
			}
		}
	}

	private void edgeResolving(ProcessAutomate<?> subProcess) {
		for (CouplingGroup entry : this.graph.getRelatedGroups()) {
			if (entry.getCriteria().name().equals(subProcess.name())) {
				LOG.info("Resolve edges of type " + subProcess.name());
				Class<? extends CriteriaScorer> scorer = entry.getScorer();
				try {
					CriteriaScorer criteriaScorer = scorer.getConstructor().newInstance();
					criteriaScorer.getScores(entry, this.graph);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Set<Node> convertToNodes(EntityRelationDiagram er) {
		Set<Node> nodes = new HashSet<>();
		for (Entity entity : er.getEntities()) {
			for (String attr : entity.getAttributes()) {
				Instance n = new Instance(attr, entity.getName());
				nodes.add(new Node(n));
			}
		}
		return nodes;
	}

	@Override
	public Solver getSolver(SolverConfiguration config) {
		return null;
	}

	@Override
	public AdjacencyMatrix getCurrentGraphState() {
		if (this.graph == null) {
			return null;
		}
		return this.graph.convertToMatrix();
	}
}
