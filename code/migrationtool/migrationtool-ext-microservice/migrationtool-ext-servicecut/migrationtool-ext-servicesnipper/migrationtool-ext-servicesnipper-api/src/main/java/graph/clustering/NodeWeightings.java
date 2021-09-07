package graph.clustering;

/**
 * Class to differentiate between the different mode of the
 * {@link ClusterAlgorithms#CHINESE_WHISPERS}
 */
public enum NodeWeightings {
	/**
	 * The node weighting approach that chooses the label with the highest total
	 * edge weight in the neighborhood.
	 */
	TOP,

	/**
	 * The node weighting approach that chooses the label with the highest total
	 * edge weight in the neighborhood divided by the logarithm of the neighbor node
	 * degree.
	 */
	LOG,

	/**
	 * The node weighting approach that chooses the label with the highest total
	 * edge weight in the neighborhood divided by the neighbor node degree.
	 */
	LIN
}
