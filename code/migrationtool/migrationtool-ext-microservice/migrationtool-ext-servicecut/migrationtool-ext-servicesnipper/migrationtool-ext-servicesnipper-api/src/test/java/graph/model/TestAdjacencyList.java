package graph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import model.criteria.CouplingCriteria;

public class TestAdjacencyList {

	@Test
	public void testAdjacencyListVertexCreation() {
		AdjacencyList list = new AdjacencyList();
		list.addNewVertex("test1");
		list.addNewVertex("test2");
		list.addNewVertex("test31");
		list.addNewVertex("test31");
		assertEquals(3, list.getAllVertices().size());
	}

	@Test
	public void testAdjacencyListEdgeCreation() {
		AdjacencyList list = new AdjacencyList();
		list.addNewVertex("test1");
		list.addNewVertex("test2");
		list.addNewEdge("test1", "test2");
		list.addNewEdge("test2", "test1");
		list.addNewEdge("test1", "test3");
		assertEquals(1, list.getAllEdges().values().stream().filter(x -> !x.isEmpty()).mapToInt(Set::size).sum());
	}

	@Test
	public void testAdjacencyListNeighbours() {
		AdjacencyList list = new AdjacencyList();
		list.addNewVertex("test1");
		list.addNewVertex("test2");
		list.addNewVertex("test3");
		list.addNewEdge("test1", "test2");
		list.addNewEdge("test3", "test2");
		assertEquals(1, list.getAllNeighbours("test1").size());
		assertEquals(2, list.getAllNeighbours("test2").size());
	}

	@Test
	public void testAdjacencyListAttribute() {
		AdjacencyList list = new AdjacencyList();
		list.addNewVertex("test1");
		list.addNewVertex("test2");
		list.addNewEdge("test1", "test2", EdgeAttribute.MIXED_ACCESS);
		assertEquals(true, list.hasEdgeAttribute("test1", "test2", EdgeAttribute.MIXED_ACCESS));
		assertEquals(false, list.hasEdgeAttribute("test1", "test2", EdgeAttribute.READ_ACCESS));
	}

	@Test
	public void testAdjacencyListScore() {
		AdjacencyList list = new AdjacencyList();
		list.addNewVertex("test1");
		list.addNewVertex("test2");
		list.addNewEdge("test1", "test2", EdgeAttribute.MIXED_ACCESS);
		list.addEdgeScore("test1", "test2", CouplingCriteria.AVAILABILITY_CRITICALITY, 3d);
		list.addEdgeScore("test1", "test2", CouplingCriteria.CONSISTENCY_CRITICALITY, 6d);
		assertEquals(9, list.getCurrentWeight("test1", "test2"));
		assertEquals(3, list.getCurrentWeightOfCriteria("test1", "test2", CouplingCriteria.AVAILABILITY_CRITICALITY));
		assertNull(list.getCurrentWeightOfCriteria("test1", "test2", CouplingCriteria.LATENCY));
	}
}
