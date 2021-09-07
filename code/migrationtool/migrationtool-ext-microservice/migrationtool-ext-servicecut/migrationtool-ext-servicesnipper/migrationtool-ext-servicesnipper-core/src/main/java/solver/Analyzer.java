package solver;

import static model.artifacts.ArchitectureArtifact.USE_CASE;
import static model.criteria.CouplingCriteria.SEMANTIC_PROXIMITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.CouplingGroup;
import model.EdgeWrapper;
import model.Graph;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.data.UseCase;
import model.priorities.Priorities;
import model.serviceDefintion.Direction;
import model.serviceDefintion.Service;
import model.serviceDefintion.ServiceRelation;

/**
 * Utils class to define the related use case and connections between clusters
 */
public class Analyzer {

	/** Score value of the write attribute of an use case */
	private static final double SCORE_WRITE = 1d;
	/** Score value of the read attribute of an use case */
	private static final double SCORE_READ = 0.25d;

	/**
	 * Defines for a each service the related use cases and connections to other
	 * clusters
	 *
	 * @param res        current result object
	 * @param graph      current graph
	 * @param priorities priorities
	 * @return filled result object
	 */
	public static Result analyseResult(Result res, Graph graph, Map<CouplingCriteria, Priorities> priorities) {
		// Find Responsible useCases
		findResponsibleServices(res, graph);
		// relation scores and shared Nanoentities
		findRelatedServices(res, graph, priorities);
		return res;
	}

	private static void findResponsibleServices(Result res, Graph graph) {
		List<CouplingGroup> useCases = graph.getRelatedGroups().stream()
				.filter(x -> x.getArtifact().equals(USE_CASE)).collect(Collectors.toList());
		List<Service> services = res.getIsolatedServices().getServices();
		for (CouplingGroup group : useCases) {
			Double highestScore = 0d;
			Service responsible = null;
			for (Service service : services) {
				long countRead = service.getInstances().stream()
						.filter(x -> group.getOrigins().contains(x.getQualifiedName()))
						.count();
				long countWritten = service.getInstances().stream()
						.filter(x -> group.getDestinations().contains(x.getQualifiedName())).count();
				double score = (countRead * SCORE_READ) + (countWritten * SCORE_WRITE);
				if (score > highestScore) {
					highestScore = score;
					responsible = service;
				}
			}
			if (responsible == null) {
				// Second check, there is no edge only one node
				for (Service service : services) {
					long count = service.getInstances().stream()
							.filter(x -> group.getRelatedNodes().contains(x.getQualifiedName()))
							.count();
					double score = count;
					if (score > highestScore) {
						highestScore = score;
						responsible = service;
					}
				}
			}
			Map<Service, List<UseCase>> map = res.getIsolatedServices().getRelatedUseCases();
			UseCase useCase = new UseCase();
			useCase.setInput(group.getOrigins().stream().map(Instance::new).collect(Collectors.toList()));
			useCase.setPersistenceChanges(
					group.getDestinations().stream().map(Instance::new).collect(Collectors.toList()));
			useCase.setLatencyCritical(group.getCriteria().equals(CouplingCriteria.LATENCY));
			useCase.setName(group.getGroupName());
			if (map.containsKey(responsible)) {
				map.get(responsible).add(useCase);
			} else {
				List<UseCase> list = new ArrayList<>(Collections.singletonList(useCase));
				map.put(responsible, list);
			}
		}
	}

	private static void findRelatedServices(Result res, Graph graph, Map<CouplingCriteria, Priorities> priorities) {
		Map<Service, List<UseCase>> useCases = res.getIsolatedServices().getRelatedUseCases();
		List<Service> serviceList = new ArrayList<>(res.getIsolatedServices().getServices());
		List<ServiceRelation> relations = res.getIsolatedServices().getRelations();

		for (int a = 0; a < (serviceList.size() - 1); a++) {
			for (int b = a + 1; b < serviceList.size(); b++) {
				Service serviceA = serviceList.get(a);
				Service serviceB = serviceList.get(b);
				ServiceRelation relation = createServiceRelation(serviceA, serviceB, useCases);
				if ((getScore(serviceA, serviceB, graph, priorities) > 0) && !relation.getSharedEntities().isEmpty()) {
					relations.add(relation);
				}
			}
		}

	}

	private static ServiceRelation createServiceRelation(Service serviceA, Service serviceB,
			Map<Service, List<UseCase>> useCases) {
		Set<Instance> shared = new HashSet<>();
		Set<Instance> aToB = getSharedInstances(useCases.get(serviceA), serviceB);
		shared.addAll(aToB);
		Set<Instance> bToA = getSharedInstances(useCases.get(serviceB), serviceA);
		shared.addAll(bToA);

		Direction direction = null;
		if (!aToB.isEmpty() && !bToA.isEmpty()) {
			direction = Direction.BIDIRECTIONAL;
		} else if (!aToB.isEmpty()) {
			direction = Direction.OUTGOING;
		} else if (!bToA.isEmpty()) {
			direction = Direction.INCOMING;
		}
		ServiceRelation relation = new ServiceRelation();
		relation.setDirection(direction);
		relation.setSharedEntities(shared);
		relation.setServiceIdA(serviceA.getName());
		relation.setServiceIdB(serviceB.getName());
		return relation;
	}

	private static Set<Instance> getSharedInstances(List<UseCase> relatedUseCases, Service comparedService) {
		if ((relatedUseCases == null) || relatedUseCases.isEmpty()) {
			return Collections.emptySet();
		}
		Set<Instance> res = new HashSet<>();
		for (UseCase useCase : relatedUseCases) {
			Set<Instance> allInstances = new HashSet<>();
			allInstances.addAll(useCase.getInput());
			allInstances.addAll(useCase.getPersistenceChanges());
			Set<Instance> filtered = allInstances.stream().filter(x -> comparedService.getInstances().contains(x))
					.collect(Collectors.toSet());
			res.addAll(filtered);
		}
		return res;
	}

	private static double getScore(Service serviceA, Service serviceB, Graph graph,
			Map<CouplingCriteria, Priorities> priorities) {
		double score = 0d;
		for (Instance instanceA : serviceA.getInstances()) {
			for (Instance instanceB : serviceB.getInstances()) {
				EdgeWrapper searchedEdge = new EdgeWrapper(instanceA.getQualifiedName(), instanceB.getQualifiedName());
				if (graph.hasEdge(searchedEdge)) {
					Double scoreSemanticProx = graph.getEdgeWeight(searchedEdge, SEMANTIC_PROXIMITY);
					if (scoreSemanticProx != null) {
						score += scoreSemanticProx * priorities.get(SEMANTIC_PROXIMITY).getValue();
					}
				}
			}
		}
		return score;
	}
}
