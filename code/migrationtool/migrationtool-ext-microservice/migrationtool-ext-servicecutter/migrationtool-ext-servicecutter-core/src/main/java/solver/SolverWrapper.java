package solver;

/**
 * Implement this class to support an algorithm in the Service Cutter.
 *
 * This class utilizes the Template Method pattern.
 *
 * @param <N> the class for a Node
 * @param <E> the class for an Edge
 */
public abstract class SolverWrapper<N, E> implements Solver {

//	private Map<EntityPair, Map<String, Score>> scores;
//
//	public SolverWrapper(final UserSystem userSystem, final Map<EntityPair, Map<String, Score>> scores) {
//		this.userSystem = userSystem;
//		this.scores = scores;
//		log.info("Created solver of type {}", getClass());
//	}
//
//	/**
//	 * Create a new node on the graph
//	 *
//	 * @param name the name of the node
//	 */
//	protected abstract void createNode(String name);
//
//	/**
//	 * Get a node from the graph
//	 *
//	 * @param nanoentity the originating nanoentity
//	 * @return
//	 */
//	protected N getNode(final Instance nanoentity) {
//		return getNode(createNodeIdentifier(nanoentity));
//	}
//
//	/**
//	 * Get a node from the graph
//	 *
//	 * @param name the given name of a nanoentity
//	 * @return
//	 */
//	protected abstract N getNode(String name);
//
//	/**
//	 * Create a new edge and set a weight on it
//	 *
//	 * @param first  the first side of the edge
//	 * @param second the second side of the edge
//	 * @param weight a weight that is used to persist the score
//	 */
//	protected abstract void createEdgeAndSetWeight(Instance first, Instance second, double weight);
//
//	/**
//	 * Remove an edge from the graph
//	 *
//	 * @param edge the edge to be removed
//	 */
//	protected abstract void removeEdge(E edge);
//
//	/**
//	 * Get an existing edge from the graph
//	 *
//	 * @param first  the first side of the edge
//	 * @param second the second side of the edge
//	 * @return the edge
//	 */
//	protected abstract E getEdge(final Nanoentity first, final Nanoentity second);
//
//	/**
//	 * @return all edges in the graph
//	 */
//	protected abstract Iterable<E> getEdges();
//
//	/**
//	 * @param edge
//	 * @return the weight representing the score
//	 */
//	protected abstract double getWeight(E edge);
//
//	/**
//	 * Set a weight representing the score
//	 *
//	 * @see #getWeight(Object)
//	 * @see #createEdgeAndSetWeight(Nanoentity, Nanoentity, double)
//	 * @param edge
//	 * @param weight weight representing the score
//	 */
//	protected abstract void setWeight(E edge, double weight);
//
//	protected void buildNodes() {
//		// create nodes
////		for (Instance nanoentity : userSystem.getNanoentities()) {
////			createNode(createNodeIdentifier(nanoentity));
////		}
//	}
//
//	protected void buildEdges() {
//		for (Entry<EntityPair, Map<String, Score>> entry : this.scores.entrySet()) {
//			setWeight(entry.getKey().nanoentityA, entry.getKey().nanoentityB,
//					entry.getValue().values().stream().mapToDouble(Score::getPrioritizedScore).sum());
//			// Logging
//			log.info("Score for nanoentity tuple {}", entry.getKey());
//			for (Entry<String, Score> criteriaScores : entry.getValue().entrySet()) {
//				log.info("{}: {} with priority {} results in {}", criteriaScores.getKey(),
//						criteriaScores.getValue().getScore(), criteriaScores.getValue().getPriority(),
//						criteriaScores.getValue().getPrioritizedScore());
//			}
//			log.info("---------------------------------------------------");
//		}
//
//		deleteNegativeEdges();
//	}
//
//	protected String createNodeIdentifier(final Instance nanoentity) {
//		return nanoentity.getContext();
//	}
//
//	private void deleteNegativeEdges() {
//		List<E> edgesToRemove = new ArrayList<>();
//
//		for (E edge : getEdges()) {
//			if (getWeight(edge) <= 0) {
//				edgesToRemove.add(edge);
//			}
//		}
//		log.info("Deleting {} edges with zero or negative weight", edgesToRemove.size());
//		for (E edge : edgesToRemove) {
//			removeEdge(edge);
//
//		}
//	}
//
//	protected void setWeight(final Instance first, final Instance second, final double weight) {
//		N nodeA = getNode(first);
//		N nodeB = getNode(second);
//		E existingEdge = getEdge(first, second);
//		if (existingEdge != null) {
//			log.info("add {} to weight of edge from node {} to {}", weight, nodeA, nodeB);
//			setWeight(existingEdge, weight);
//		} else {
//			log.info("create edge with weight {} from node {} to {}", weight, nodeA, nodeB);
//			createEdgeAndSetWeight(first, second, weight);
//		}
//
//	}

}
