package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import model.Edge;
import model.EdgeAttribute;
import model.Node;
import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;
import model.data.ArchitectureInformation;
import model.data.Characteristic;
import model.data.ContextGroup;
import model.data.Instance;
import model.data.UseCase;
import model.erm.Entity;
import model.erm.EntityRelation;
import model.erm.EntityRelationDiagram;
import model.erm.RelationType;

public class Processor {

	private Processor() {

	}

	public static Map<String, Set<Edge>> createEdgesSameContext(EntityRelationDiagram er) {
		Map<String, Set<Edge>> edges = new HashMap<>();
		for (Entity entity : er.getEntities()) {
			List<Instance> nodes = new ArrayList<>();
			for (int i = entity.getAttributes().size() - 1; i >= 0; i--) {
				nodes.add(new Instance(entity.getAttributes().get(i), entity.getName()));
			}
			edges.put(entity.getName(), createEdgesFromSameContext(nodes));
		}
		return edges;
	}

	public static Map<String, Set<Edge>> createEdgesRelationship(EntityRelationDiagram er, RelationType type) {
		List<EntityRelation> list = er.getRelations().stream().filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
		Map<String, Set<Edge>> edges = new HashMap<>();
		for (EntityRelation relation : list) {
			String id = type.toString() + "/" + relation.getOrigin().getName() + "/"
					+ relation.getDestination().getName();
			edges.put(id, createEdgesFromRelation(relation));
		}
		return edges;
	}

	public static Map<String, Set<Edge>> createEdgesUseCase(ArchitectureInformation ai) {
		return createEdgesUseCases(ai, false);
	}

	public static Map<String, Set<Edge>> createEdgesLatency(ArchitectureInformation ai) {
		return createEdgesUseCases(ai, true);
	}

	private static Map<String, Set<Edge>> createEdgesUseCases(ArchitectureInformation ai, boolean latency) {
		List<UseCase> useCases = ai.getUseCases().stream().filter(x -> {
			boolean temp = Optional.ofNullable(x.isLatencyCritical()).orElse(false);
			return latency ? temp : !temp;
		}).collect(Collectors.toList());
		Map<String, Set<Edge>> edges = new HashMap<>();
		for (UseCase useCase : useCases) {
			Set<Edge> tmp = createEdgesFromSemanticRelation(useCase.getInput(), useCase.getPersistenceChanges());
			edges.put(useCase.getName(), tmp);
		}
		return edges;
	}

	public static Map<String, Set<Edge>> createEdgesContextGroup(ArchitectureInformation ai,
			ArchitectureArtifact criteria) {
		Map<String, Set<Edge>> edges = new HashMap<>();
		List<ContextGroup> contextGroups = ai.getCriteria().get(criteria);
		if (contextGroups != null) {
			for (ContextGroup contextGroup : contextGroups) {
				edges.put(contextGroup.getName(), createEdgesFromSameContext(contextGroup.getInstances()));
			}
		}
		return edges;
	}

	public static Map<String, Set<Edge>> createEdgesCharacteristics(ArchitectureInformation ai,
			CouplingCriteria criteria) {
		Map<String, Set<Edge>> edges = new HashMap<>();
		List<Characteristic> characteristics = ai.getCompatibilities().get(criteria);
		if (characteristics != null) {
			for (Characteristic characteristic : characteristics) {
				edges.put(criteria.toString() + "/" + characteristic.getCompabilityCharacteristics(),
						createEdgesFromSameContext(characteristic.getInstances()));
			}
		}
		return edges;
	}

	private static Set<Edge> createEdgesFromRelation(EntityRelation relation) {
		Set<Edge> edges = new HashSet<>();
		Entity origin = relation.getOrigin();
		Entity destination = relation.getDestination();
		for (String attrOrig : origin.getAttributes()) {
			for (String attrDest : destination.getAttributes()) {
				Node node1 = new Node(new Instance(attrOrig, origin.getName()));
				Node node2 = new Node(new Instance(attrDest, destination.getName()));
				edges.add(new Edge(node1, node2));
			}
		}
		return edges;
	}

	private static Set<Edge> createEdgesFromSemanticRelation(List<Instance> origin, List<Instance> destinations) {
		Set<Edge> edges = new HashSet<>();
		// READ ACCESS
		for (Instance attrOrig : origin) {
			for (Instance attrDest : origin) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				Edge edge = new Edge(new Node(attrOrig), new Node(attrDest));
				edge.getAttributes().add(EdgeAttribute.READ_ACCESS);
				edges.add(edge);
			}
		}
		// MIXED ACCESS
		for (Instance attrOrig : origin) {
			for (Instance attrDest : destinations) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				Edge edge = new Edge(new Node(attrOrig), new Node(attrDest));
				edge.getAttributes().add(EdgeAttribute.MIXED_ACCESS);
				edges.add(edge);
			}
		}
		// WRITTEN ACCESS
		for (Instance attrOrig : destinations) {
			for (Instance attrDest : destinations) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				Edge edge = new Edge(new Node(attrOrig), new Node(attrDest));
				edge.getAttributes().add(EdgeAttribute.WRITE_ACCESS);
				edges.add(edge);
			}
		}
		return edges;
	}

	private static Set<Edge> createEdgesFromSameContext(List<Instance> origin) {
		Set<Edge> edges = new HashSet<>();
		for (int i = origin.size() - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				Edge edge = new Edge(new Node(origin.get(i)), new Node(origin.get(j)));
				edges.add(edge);
			}
		}
		return edges;
	}
}
