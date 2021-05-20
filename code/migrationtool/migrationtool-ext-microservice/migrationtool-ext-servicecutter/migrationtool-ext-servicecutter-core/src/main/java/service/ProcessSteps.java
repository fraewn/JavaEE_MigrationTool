package service;

import static model.criteria.ArchitectureArtifact.AGGREGATES;
import static model.criteria.ArchitectureArtifact.COMPATIBILITIES;
import static model.criteria.ArchitectureArtifact.ENTITIES;
import static model.criteria.ArchitectureArtifact.PREDEFINED_SERVICES;
import static model.criteria.ArchitectureArtifact.RELATIONSHIPS;
import static model.criteria.ArchitectureArtifact.SECURITY_ACCESS_GROUPS;
import static model.criteria.ArchitectureArtifact.SEPERATED_SECURITY_ZONES;
import static model.criteria.ArchitectureArtifact.USE_CASE;
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
import java.util.Set;

import core.Edge;
import model.ModelRepresentation;
import model.erm.RelationType;
import processing.GraphCreationSteps;
import processing.ProcessAutomate;
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
			Map<String, Set<Edge>> edges = Processor.createEdgesSameContext(rep.getEntityDiagram());
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
			Map<String, Set<Edge>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesRelationship(rep.getEntityDiagram(),
					RelationType.AGGREGATION);
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
			Map<String, Set<Edge>> edges = Processor.createEdgesUseCase(rep.getInformation());
			return new StepInformation(edges, USE_CASE, SEMANTIC_PROXIMITY, CSSemanticProximity.class);
		}
	},

	CREATE_EDGES_LATENCY {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.LATENCY;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<Edge>> edges = Processor.createEdgesLatency(rep.getInformation());
			return new StepInformation(edges, USE_CASE, LATENCY, CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_AGGREGATES {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.AGGREGATES;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<Edge>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					CONSISTENCY_CONSTRAINT);
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
			Map<String, Set<Edge>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					PREDEFINED_SERVICE_CONSTRAINT);
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
			Map<String, Set<Edge>> edges = Processor.createEdgesContextGroup(rep.getInformation(), SHARED_OWNER);
			return new StepInformation(edges, PREDEFINED_SERVICES, PREDEFINED_SERVICE_CONSTRAINT,
					CSCohesiveGroup.class);
		}
	},

	CREATE_EDGES_SECURITY_ZONES {
		@Override
		public GraphCreationSteps getStepName() {
			return GraphCreationSteps.SECURITY_ZONES;
		}

		@Override
		public StepInformation execute(ModelRepresentation rep) {
			Map<String, Set<Edge>> edges = Processor.createEdgesContextGroup(rep.getInformation(), SECURITY_CONSTRAINT);
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
			Map<String, Set<Edge>> edges = Processor.createEdgesContextGroup(rep.getInformation(),
					SECURITY_CONTEXUALITY);
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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
			Map<String, Set<Edge>> edges = Processor.createEdgesCharacteristics(rep.getInformation(),
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

	public static ProcessSteps getStep(ProcessAutomate<?> currentStep) {
		for (ProcessSteps i : ProcessSteps.values()) {
			if (i.getStepName().name().equals(currentStep.name())) {
				return i;
			}
		}
		// should not be called
		throw new RuntimeException("Unexpected step");
	}

	public abstract GraphCreationSteps getStepName();

	public abstract StepInformation execute(ModelRepresentation rep);
}
