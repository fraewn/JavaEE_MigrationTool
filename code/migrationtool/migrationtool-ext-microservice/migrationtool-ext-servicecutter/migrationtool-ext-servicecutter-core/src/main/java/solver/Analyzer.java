package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import model.CouplingGroup;
import model.Edge;
import model.Graph;
import model.Node;
import model.Result;
import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.data.UseCase;
import model.priorities.Priorities;
import model.serviceDefintion.Direction;
import model.serviceDefintion.Service;
import model.serviceDefintion.ServiceRelation;

public class Analyzer {

	private static final double SCORE_WRITE = 1d;
	private static final double SCORE_READ = 0.25d;

	public static Result analyseResult(Result res, Graph graph, Map<CouplingCriteria, Priorities> priorities) {
		// Find Responsible useCases
		findResponsibleServices(res, graph);
		// relation scores and shared Nanoentities
		findRelatedServices(res, graph, priorities);
		return res;
	}

	private static void findResponsibleServices(Result res, Graph graph) {
		List<CouplingGroup> useCases = graph.getRelatedGroups().stream()
				.filter(x -> x.getArtifact().equals(ArchitectureArtifact.USE_CASE)).collect(Collectors.toList());
		List<Service> services = res.getIsolatedServices().getServices();
		for (CouplingGroup group : useCases) {
			Double highestScore = 0d;
			Service responsible = null;
			for (Service service : services) {
				long countRead = service.getInstances().stream().filter(x -> group.getOrigins().contains(new Node(x)))
						.count();
				long countWritten = service.getInstances().stream()
						.filter(x -> group.getDestinations().contains(new Node(x))).count();
				double score = (countRead * SCORE_READ) + (countWritten * SCORE_WRITE);
				if (score > highestScore) {
					highestScore = score;
					responsible = service;
				}
			}
			if (responsible == null) {
				// Second check, there is no edge only one node
				for (Service service : services) {
					long count = service.getInstances().stream().filter(x -> group.getRelatedNodes().contains(x))
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
			useCase.setInput(group.getOrigins().stream().map(Node::getInstance).collect(Collectors.toList()));
			useCase.setPersistenceChanges(
					group.getDestinations().stream().map(Node::getInstance).collect(Collectors.toList()));
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
				Edge searchedEdge = new Edge(new Node(instanceA), new Node(instanceB));
				if (graph.hasEdge(searchedEdge)) {
					Map<CouplingCriteria, Double> scores = graph.getEdge(searchedEdge).getWeight().getWeight();
					List<CouplingCriteria> list = Collections.singletonList(CouplingCriteria.SEMANTIC_PROXIMITY);
					for (CouplingCriteria criteria : list) {
						if (scores.containsKey(criteria)) {
							score += scores.get(criteria) * priorities.get(criteria).getValue();
						}
					}
				}
			}
		}
		return score;
	}
}
