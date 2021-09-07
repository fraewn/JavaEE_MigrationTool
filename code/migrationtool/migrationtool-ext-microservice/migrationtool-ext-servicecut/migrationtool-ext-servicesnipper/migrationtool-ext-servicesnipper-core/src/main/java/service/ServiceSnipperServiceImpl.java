package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graph.clustering.ClusterAlgorithms;
import graph.clustering.SolverConfiguration;
import graph.model.AdjacencyList;
import graph.model.GraphModel;
import graph.processing.GraphProcessingSteps;
import model.CouplingGroup;
import model.EdgeWrapper;
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

/**
 * Implementation of the service interface
 */
public class ServiceSnipperServiceImpl implements ServiceSnipperService {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Reference to the current graph */
	private Graph graph;
	/** Reference to the input model */
	private ModelRepresentation rep;
	/** Refrence to the result object */
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

	private void nodeCreation(EntityRelationDiagram er) {
		LOG.info("Creating nodes");
		for (Entity entity : er.getEntities()) {
			for (String attr : entity.getAttributes()) {
				Instance n = new Instance(attr, entity.getName());
				this.graph.addNewVertex(n.getQualifiedName());
			}
		}
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
		ProcessSteps processing = ProcessSteps.getStep(subProcess);
		StepInformation info = processing.execute(this.rep);
		for (CouplingGroup group : info.getGroups()) {
			LOG.info("Group created " + group.getGroupName() + " has " + group.getRelatedEdges().size() + " edges");
			this.graph.addRelatedGroup(group);
			for (EdgeWrapper edge : group.getRelatedEdges()) {
				this.graph.addNewEdge(edge, edge.getAttr());
			}
		}
	}

	private void edgeResolving(Enum<?> subProcess) {
		LOG.info("Resolve edges of type " + subProcess.name());
		CouplingCriteria currentCriteria = CouplingCriteria.valueOf(subProcess.name().toUpperCase());
		Map<EdgeWrapper, Double> resolvedValues = new HashMap<>();
		CriteriaScorer currentScorer = null;
		// Run all groups of the searched criteria
		for (CouplingGroup entry : this.graph.getRelatedGroups()) {
			if (entry.getCriteria().name().equals(subProcess.name())) {
				LOG.info("Related Group: " + entry.getGroupName());
				// Scorer and criteria is for all groups in the step the same
				currentScorer = CriteriaScorerFactory.getScorer(entry.getScorer());
				Map<EdgeWrapper, Double> values = currentScorer.getScores(entry, this.graph);
				for (Entry<EdgeWrapper, Double> value : values.entrySet()) {
					if (resolvedValues.containsKey(value.getKey())) {
						resolvedValues.put(value.getKey(), resolvedValues.get(value.getKey()) + value.getValue());
					} else {
						resolvedValues.put(value.getKey(), value.getValue());
					}
				}
			}
		}
		if (!resolvedValues.isEmpty()) {
			LOG.info("Normailze edges of type " + subProcess.name());
			Map<EdgeWrapper, Double> res = currentScorer.normalize(resolvedValues);
			for (Entry<EdgeWrapper, Double> e : res.entrySet()) {
				this.graph.addNewScore(e.getKey(), currentCriteria, e.getValue());
			}
		}
	}

	@Override
	public GraphModel getCurrentGraphState() {
		if (this.graph == null) {
			return null;
		}
		return this.graph.getGraphModel();
	}

	@Override
	public Result solveCluster(ClusterAlgorithms algo, SolverConfiguration config,
			Map<CouplingCriteria, Priorities> priorities) {
		Solver solver = SolverFactory.getSolver(algo);
		this.result = solver.solve(this.graph, priorities, config);
		return this.result;
	}

	@Override
	public GraphModel getCurrentResultGraphState() {
		if (this.result == null) {
			return null;
		}
		GraphModel adjList = new AdjacencyList();
		for (Service service : this.result.getIsolatedServices().getServices()) {
			adjList.addNewVertex(service.getName());
			for (Instance relatedNode : service.getInstances()) {
				adjList.addNewVertex(relatedNode.getQualifiedName());
				adjList.addNewEdge(service.getName(), relatedNode.getQualifiedName());
			}
		}
		for (ServiceRelation relation : this.result.getIsolatedServices().getRelations()) {
			adjList.addNewEdge(relation.getServiceIdA(), relation.getServiceIdB());
		}
		return adjList;
	}
}
