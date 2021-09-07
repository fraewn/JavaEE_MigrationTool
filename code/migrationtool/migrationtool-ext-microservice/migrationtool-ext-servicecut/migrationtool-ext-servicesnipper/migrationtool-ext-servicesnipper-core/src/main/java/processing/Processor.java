package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import graph.model.EdgeAttribute;
import model.EdgeWrapper;
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

/**
 * Helper class to create edges based of the context
 */
public class Processor {

	private Processor() {

	}

	/**
	 * Creates the edges for type same context
	 *
	 * @param er er diagram
	 * @return list of created edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesSameContext(EntityRelationDiagram er) {
		Map<String, Set<EdgeWrapper>> edges = new HashMap<>();
		for (Entity entity : er.getEntities()) {
			List<String> nodes = new ArrayList<>();
			for (int i = entity.getAttributes().size() - 1; i >= 0; i--) {
				nodes.add(new Instance(entity.getAttributes().get(i), entity.getName()).getQualifiedName());
			}
			edges.put(entity.getName(), createEdgesFromSameContext(nodes));
		}
		return edges;
	}

	/**
	 * Creates the edges for type relationship
	 *
	 * @param er   er diagram
	 * @param type relationship type
	 * @return list of created edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesRelationship(EntityRelationDiagram er, RelationType type) {
		List<EntityRelation> list = er.getRelations().stream().filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
		Map<String, Set<EdgeWrapper>> edges = new HashMap<>();
		for (EntityRelation relation : list) {
			String id = type.toString() + "/" + relation.getOrigin().getName() + "/"
					+ relation.getDestination().getName();
			edges.put(id, createEdgesFromRelation(relation));
		}
		return edges;
	}

	/**
	 * Creates the edges for type use case
	 *
	 * @param ai model information
	 * @return list of created edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesUseCase(ArchitectureInformation ai) {
		return createEdgesUseCases(ai, false);
	}

	/**
	 * Creates the edges for type latency
	 *
	 * @param ai model information
	 * @return list of created edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesLatency(ArchitectureInformation ai) {
		return createEdgesUseCases(ai, true);
	}

	private static Map<String, Set<EdgeWrapper>> createEdgesUseCases(ArchitectureInformation ai, boolean latency) {
		List<UseCase> useCases = ai.getUseCases().stream().filter(x -> {
			boolean temp = Optional.ofNullable(x.isLatencyCritical()).orElse(false);
			return latency ? temp : !temp;
		}).collect(Collectors.toList());
		Map<String, Set<EdgeWrapper>> edges = new HashMap<>();
		for (UseCase useCase : useCases) {
			Set<EdgeWrapper> tmp = createEdgesFromSemanticRelation(useCase.getInput(), useCase.getPersistenceChanges());
			edges.put(useCase.getName(), tmp);
		}
		return edges;
	}

	/**
	 * Creates the edges for a context group
	 *
	 * @param ai       model information
	 * @param criteria coupling criteria
	 * @return list of edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesContextGroup(ArchitectureInformation ai,
			ArchitectureArtifact criteria) {
		Map<String, Set<EdgeWrapper>> edges = new HashMap<>();
		List<ContextGroup> contextGroups = ai.getCriteria().get(criteria);
		if (contextGroups != null) {
			for (ContextGroup contextGroup : contextGroups) {
				List<String> group = contextGroup.getInstances().stream().map(Instance::getQualifiedName)
						.collect(Collectors.toList());
				edges.put(contextGroup.getName(), createEdgesFromSameContext(group));
			}
		}
		return edges;
	}

	/**
	 * Creates the edges for a coupling criteria of type compatibility
	 *
	 * @param ai       model information
	 * @param criteria coupling criteria
	 * @return list of edges
	 */
	public static Map<String, Set<EdgeWrapper>> createEdgesCharacteristics(ArchitectureInformation ai,
			CouplingCriteria criteria) {
		Map<String, Set<EdgeWrapper>> edges = new HashMap<>();
		List<Characteristic> characteristics = ai.getCompatibilities().get(criteria);
		if (characteristics != null) {
			for (Characteristic characteristic : characteristics) {
				List<String> group = characteristic.getInstances().stream().map(Instance::getQualifiedName)
						.collect(Collectors.toList());
				edges.put(criteria.toString() + "/" + characteristic.getCompabilityCharacteristics(),
						createEdgesFromSameContext(group));
			}
		}
		return edges;
	}

	private static Set<EdgeWrapper> createEdgesFromRelation(EntityRelation relation) {
		Set<EdgeWrapper> edges = new HashSet<>();
		Entity origin = relation.getOrigin();
		Entity destination = relation.getDestination();
		for (String attrOrig : origin.getAttributes()) {
			for (String attrDest : destination.getAttributes()) {
				Instance node1 = new Instance(attrOrig, origin.getName());
				Instance node2 = new Instance(attrDest, destination.getName());
				edges.add(new EdgeWrapper(node1.getQualifiedName(), node2.getQualifiedName()));
			}
		}
		return edges;
	}

	private static Set<EdgeWrapper> createEdgesFromSemanticRelation(List<Instance> origin,
			List<Instance> destinations) {
		Set<EdgeWrapper> edges = new HashSet<>();
		// READ ACCESS
		for (Instance attrOrig : origin) {
			for (Instance attrDest : origin) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				EdgeWrapper edge = new EdgeWrapper(attrOrig.getQualifiedName(), attrDest.getQualifiedName());
				edge.setAttr(EdgeAttribute.READ_ACCESS);
				edges.add(edge);
			}
		}
		// MIXED ACCESS
		for (Instance attrOrig : origin) {
			for (Instance attrDest : destinations) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				EdgeWrapper edge = new EdgeWrapper(attrOrig.getQualifiedName(), attrDest.getQualifiedName());
				edge.setAttr(EdgeAttribute.MIXED_ACCESS);
				edges.add(edge);
			}
		}
		// WRITTEN ACCESS
		for (Instance attrOrig : destinations) {
			for (Instance attrDest : destinations) {
				if (attrOrig.equals(attrDest)) {
					continue;
				}
				EdgeWrapper edge = new EdgeWrapper(attrOrig.getQualifiedName(), attrDest.getQualifiedName());
				edge.setAttr(EdgeAttribute.WRITE_ACCESS);
				edges.add(edge);
			}
		}
		return edges;
	}

	private static Set<EdgeWrapper> createEdgesFromSameContext(List<String> origin) {
		Set<EdgeWrapper> edges = new HashSet<>();
		for (int i = origin.size() - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				EdgeWrapper edge = new EdgeWrapper(origin.get(i), origin.get(j));
				edges.add(edge);
			}
		}
		return edges;
	}
}
