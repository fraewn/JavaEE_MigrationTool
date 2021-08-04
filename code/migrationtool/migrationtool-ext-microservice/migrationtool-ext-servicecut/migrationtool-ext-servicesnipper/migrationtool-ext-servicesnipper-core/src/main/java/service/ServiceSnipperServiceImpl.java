package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.model.AdjacencyList;
import graph.processing.GraphProcessingSteps;
import model.CouplingGroup;
import model.Edge;
import model.Graph;
import model.ModelRepresentation;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.erm.Entity;
import model.erm.EntityRelationDiagram;
import model.priorities.Priorities;
import model.serviceDefintion.Service;
import model.serviceDefintion.ServiceRelation;
import processing.ProcessSteps;
import processing.StepInformation;
import resolver.CriteriaScorer;
import resolver.CriteriaScorerFactory;
import solver.Solver;
import solver.SolverFactory;

public class ServiceSnipperServiceImpl implements ServiceSnipperService {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private Graph graph;

	private ModelRepresentation rep;

	private Result result;

	public ServiceSnipperServiceImpl() {
		this.graph = new Graph();
	}

	@Override
	public void importArchitecture(ModelRepresentation rep) {
		this.graph = new Graph();
		this.rep = rep;
		EntityRelationDiagram er = rep.getEntityDiagram();
		nodeCreation(er);
	}

	@Override
	public void process(GraphProcessingSteps currentStep, Enum<?> subProcess) {
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

	private void edgeCreation(Enum<?> subProcess) {
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

	private void edgeResolving(Enum<?> subProcess) {
		LOG.info("Resolve edges of type " + subProcess.name());
		Map<Edge, Double> resolvedCriteria = new HashMap<>();
		CriteriaScorer currentScorer = null;
		CouplingCriteria currentCriteria = null;
		for (CouplingGroup entry : this.graph.getRelatedGroups()) {
			if (entry.getCriteria().name().equals(subProcess.name())) {
				LOG.info("Related Group: " + entry.getGroupName());
				// Scorer and criteria is for all groups in the step the same
				currentCriteria = entry.getCriteria();
				currentScorer = CriteriaScorerFactory.getScorer(entry.getScorer());
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

	private void nodeCreation(EntityRelationDiagram er) {
		LOG.info("Creating nodes");
		for (Entity entity : er.getEntities()) {
			for (String attr : entity.getAttributes()) {
				Instance n = new Instance(attr, entity.getName());
				this.graph.addNewNode(n.getQualifiedName());
			}
		}
	}

	@Override
	public AdjacencyList getCurrentGraphState() {
		if (this.graph == null) {
			return null;
		}
		return this.graph.convert();
	}

	@Override
	public Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities) {
		Solver solver = SolverFactory.getSolver(algo);
		this.result = solver.solve(this.graph, priorities, config);
		return this.result;
	}

	@Override
	public AdjacencyList getCurrentResultGraphState() {
		if (this.result == null) {
			return null;
		}
		AdjacencyList adjList = new AdjacencyList();
		for (Service service : this.result.getIsolatedServices().getServices()) {
			adjList.addNewVertex(service.getName());
			for (Instance relatedNode : service.getInstances()) {
				adjList.addNewVertex(relatedNode.getQualifiedName());
				adjList.addNewEdge(service.getName(), relatedNode.getQualifiedName(), 0d);
			}
		}
		for (ServiceRelation relation : this.result.getIsolatedServices().getRelations()) {
			adjList.addNewEdge(relation.getServiceIdA(), relation.getServiceIdB(), 0d);
		}
		return adjList;
	}
}
