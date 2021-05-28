package service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import core.CouplingGroup;
import core.Edge;
import core.Graph;
import core.Node;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.data.Priorities;
import model.erm.Entity;
import model.erm.EntityRelationDiagram;
import processing.GraphProcessingSteps;
import processing.ProcessAutomate;
import resolver.CriteriaScorer;
import resolver.Scorer;
import solver.ClusterAlgorithms;
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
		LOG.info("Resolve edges of type " + subProcess.name());
		Map<Edge, Double> resolvedCriteria = new HashMap<>();
		CriteriaScorer currentScorer = null;
		CouplingCriteria currentCriteria = null;
		for (CouplingGroup entry : this.graph.getRelatedGroups()) {
			if (entry.getCriteria().name().equals(subProcess.name())) {
				LOG.info("Related Group: " + entry.getGroupName());
				// Scorer and criteria is for all groups in the step the same
				currentCriteria = entry.getCriteria();
				currentScorer = Scorer.getScorer(entry.getScorer());
				Map<Edge, Double> values = currentScorer.getScores(entry, this.graph);
				for (Entry<Edge, Double> value : values.entrySet()) {
					if (resolvedCriteria.containsKey(value.getKey())) {
						Double current = resolvedCriteria.get(value.getKey());
						resolvedCriteria.put(value.getKey(), current + value.getValue());
					} else {
						resolvedCriteria.put(value.getKey(), value.getValue());
					}
				}
			}
		}
		if (!resolvedCriteria.isEmpty()) {
			LOG.info("Normailze edges of type " + subProcess.name());
			Map<Edge, Double> res = currentScorer.normalize(resolvedCriteria);
			for (Entry<Edge, Double> e : res.entrySet()) {
				this.graph.addNewScore(e.getKey(), currentCriteria, e.getValue());
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
	public AdjacencyMatrix getCurrentGraphState() {
		if (this.graph == null) {
			return null;
		}
		return this.graph.convertToMatrix();
	}

	@Override
	public Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities) {

		return null;
	}

	@Override
	public AdjacencyMatrix getCurrentResultGraphState() {
		// TODO Auto-generated method stub
		return null;
	}
}
