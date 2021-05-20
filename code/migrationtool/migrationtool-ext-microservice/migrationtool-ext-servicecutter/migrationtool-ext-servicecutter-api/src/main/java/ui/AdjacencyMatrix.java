package ui;

import java.util.List;

public class AdjacencyMatrix {

	private List<String> labelNodes;

	private double[][] matrix;

	/**
	 * @return the labelNodes
	 */
	public List<String> getLabelNodes() {
		return this.labelNodes;
	}

	/**
	 * @param labelNodes the labelNodes to set
	 */
	public void setLabelNodes(List<String> labelNodes) {
		this.labelNodes = labelNodes;
	}

	/**
	 * @return the matrix
	 */
	public double[][] getMatrix() {
		return this.matrix;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
}
