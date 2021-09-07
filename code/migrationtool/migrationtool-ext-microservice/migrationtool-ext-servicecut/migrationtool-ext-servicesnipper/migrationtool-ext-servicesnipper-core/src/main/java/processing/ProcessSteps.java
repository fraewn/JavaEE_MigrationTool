package processing;

import static model.artifacts.ArchitectureArtifact.AGGREGATES;
import static model.artifacts.ArchitectureArtifact.COMPATIBILITIES;
import static model.artifacts.ArchitectureArtifact.ENTITIES;
import static model.artifacts.ArchitectureArtifact.PREDEFINED_SERVICES;
import static model.artifacts.ArchitectureArtifact.RELATIONSHIPS;
import static model.artifacts.ArchitectureArtifact.SECURITY_ACCESS_GROUPS;
import static model.artifacts.ArchitectureArtifact.SEPERATED_SECURITY_ZONES;
import static model.artifacts.ArchitectureArtifact.SHARED_OWNER_GROUPS;
import static model.artifacts.ArchitectureArtifact.USE_CASE;
import static model.criteria.CouplingCriteria.AVAILABILITY_CRITICALITY;
import static model.criteria.CouplingCriteria.CONSISTENCY_CONSTRAINT;
import static model.criteria.CouplingCriteria.CONSISTENCY_CRITICALITY;
import static model.criteria.CouplingCriteria.CONTENT_VOLATILITY;
import static model.criteria.CouplingCriteria.IDENTITY_LIFECYCLE;
import static model.criteria.CouplingCriteria.LATENCY;
import static model.criteria.CouplingCriteria.PREDEFINED_SERVICE_CONSTRAINT;
import static model.criteria.CouplingCriteria.SECURITY_CONSTRAINT;
import static model.criteria.CouplingCriteria.SECURITY_CONTEXUALITY;
import static model.criteria.CouplingCriteria.SECURITY_CRITICALITY;
import static model.criteria.CouplingCriteria.SEMANTIC_PROXIMITY;
import static model.criteria.CouplingCriteria.SHARED_OWNER;
import static model.criteria.CouplingCriteria.STORAGE_SIMILARITY;
import static model.criteria.CouplingCriteria.STRUCTURAL_VOLATILITY;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import graph.model.EdgeAttribute;
import graph.processing.GraphCreationSteps;
import model.CouplingGroup;
import model.EdgeWrapper;
import model.ModelRepresentation;
import model.data.Instance;
import model.data.UseCase;
import model.erm.RelationType;
import resolver.CSCharacteristics;
import resolver.CSCohesiveGroup;
import resolver.CSExclusiveGroup;
import resolver.CSSemanticProximity;
import resolver.CSSeparatedGroup;

public enum ProcessSteps {

	CREATE_EDGES_SAME_CONTEXT {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.SAME_CONTEXT;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesSameContext(rep.getEntityDiagram());
			return new StepInformation(edges, ENTITIES, IDENTITY_LIFECYCLE, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_TYPE_INHERITANCE {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.TYPE_INHERITANCE;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
					RelationType.INHERITANCE);
			return new StepInformation(edges, RELATIONSHIPS, IDENTITY_LIFECYCLE, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_TYPE_COMPOSITION {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.TYPE_COMPOSITION;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
					RelationType.COMPOSITION);
			return new StepInformation(edges, RELATIONSHIPS, IDENTITY_LIFECYCLE, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_TYPE_AGGREGATION {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.TYPE_AGGREGATION;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
					RelationType.AGGREGATION);
			for (Entry<String, Set<EdgeWrapper>> element : edges.entrySet()) {
				for (EdgeWrapper edge : element.getValue()) {
					edge.setAttr(EdgeAttribute.AGGREGATION);
				}
			}
			return new StepInformation(edges, RELATIONSHIPS, SEMANTIC_PROXIMITY, CSSemanticProximity.class);
		}
	},

	CREATE_EDGES_USE_CASE {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.USE_CASE;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesUseCase(rep.getInformation());
			StepInformation info = new StepInformation(edges, USE_CASE, SEMANTIC_PROXIMITY, CSSemanticProximity.class);
			for (CouplingGroup group : info.getGroups()) {
				if (group.getRelatedEdges().isEmpty()) {
					for (UseCase useCase : rep.getInformation().getUseCases()) {
						if (useCase.getName().equals(group.getGroupName())) {
							group.getRelatedNodes().addAll(useCase.getInput().stream().map(Instance::getQualifiedName)
									.collect(Collectors.toList()));
							group.getRelatedNodes()
									.addAll(useCase.getPersistenceChanges().stream().map(Instance::getQualifiedName)
											.collect(Collectors.toList()));
						}
					}
				}
			}
			return info;
		}
	},

	CREATE_EDGES_LATENCY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.LATENCY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesLatency(rep.getInformation());
			StepInformation info = new StepInformation(edges, USE_CASE, LATENCY, CSCohesiveGroup.class);
			for (CouplingGroup group : info.getGroups()) {
				if (group.getRelatedEdges().isEmpty()) {
					for (UseCase useCase : rep.getInformation().getUseCases()) {
						if (useCase.getName().equals(group.getGroupName())) {
							group.getRelatedNodes().addAll(useCase.getInput().stream().map(Instance::getQualifiedName)
									.collect(Collectors.toList()));
							group.getRelatedNodes()
									.addAll(useCase.getPersistenceChanges().stream().map(Instance::getQualifiedName)
											.collect(Collectors.toList()));
						}
					}
				}
			}
			return info;
		}
	},

	CREATE_EDGES_AGGREGATES {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.AGGREGATES;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesContextGroup(rep.getInformation(), AGGREGATES);
			return new StepInformation(edges, AGGREGATES, CONSISTENCY_CONSTRAINT, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_PREDEFINED_SERVICE {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.PREDEFINED_SERVICE;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					PREDEFINED_SERVICES);
			return new StepInformation(edges, PREDEFINED_SERVICES, PREDEFINED_SERVICE_CONSTRAINT,
					CSExclusiveGroup.class);
		}
	},

	CREATE_EDGES_SHARED_OWNER {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.SHARED_OWNER;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					SHARED_OWNER_GROUPS);
			return new StepInformation(edges, SHARED_OWNER_GROUPS, SHARED_OWNER, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_SECURITY_ZONES {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.SECURITY_ZONES;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					SEPERATED_SECURITY_ZONES);
			return new StepInformation(edges, SEPERATED_SECURITY_ZONES, SECURITY_CONSTRAINT, CSSeparatedGroup.class);
		}
	},

	CREATE_EDGES_ACCESS_GROUPS {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.ACCESS_GROUPS;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					SECURITY_ACCESS_GROUPS);
			return new StepInformation(edges, SECURITY_ACCESS_GROUPS, SECURITY_CONTEXUALITY, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_CONTENT_VOLATILITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.CONTENT_VOLATILITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					CONTENT_VOLATILITY);
			return new StepInformation(edges, COMPATIBILITIES, CONTENT_VOLATILITY, CSCharacteristics.class);
		}
	},

	CREATE_EDGES_STRUCTURAL_VOLATILITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.STRUCTURAL_VOLATILITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					STRUCTURAL_VOLATILITY);
			return new StepInformation(edges, COMPATIBILITIES, STRUCTURAL_VOLATILITY, CSCharacteristics.class);
		}
	},

	CREATE_EDGES_AVAILABILITY_CRITICALITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.AVAILABILITY_CRITICALITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					AVAILABILITY_CRITICALITY);
			return new StepInformation(edges, COMPATIBILITIES, AVAILABILITY_CRITICALITY, CSCharacteristics.class);
		}
	},

	CREATE_EDGES_CONSISTENCY_CRITICALITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.CONSISTENCY_CRITICALITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					CONSISTENCY_CRITICALITY);
			return new StepInformation(edges, COMPATIBILITIES, CONSISTENCY_CRITICALITY, CSCharacteristics.class);
		}
	},

	CREATE_EDGES_STORAGE_SIMILARITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.STORAGE_SIMILARITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					STORAGE_SIMILARITY);
			return new StepInformation(edges, COMPATIBILITIES, STORAGE_SIMILARITY, CSCharacteristics.class);
		}
	},

	CREATE_EDGES_SECURITY_CRITICALITY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.SECURITY_CRITICALITY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<EdgeWrapper>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
					SECURITY_CRITICALITY);
			return new StepInformation(edges, COMPATIBILITIES, SECURITY_CRITICALITY, CSCharacteristics.class);
		}
	},

	GRAPH_CREATION_DONE {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.CREATION_DONE;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			return null;
		}
	};

	public static <E extends Enum<?>> ProcessSteps getStep(E currentStep) {
		for (ProcessSteps i : ProcessSteps.values()) {
			if (i.getStepName().name().equals(currentStep.name())) {
				return i;
			}
		}
		// should not be called
		throw new RuntimeException("Unexpected step");
	}

	/**
	 * @return mapping to graph creation steps
	 */
	public abstract GraphCreationSteps getStepName();

	/**
	 * Creates the edges based on the current model
	 *
	 * @param rep current model
	 * @return information of edge creation
	 */
	public abstract StepInformation execute(ModelRepresentation rep);
}
