package data;

public enum GraphView {

	/**
	 *
	 */
	GRAPH("graphstream/visual_settings_graph.css"),
	/**
	 *
	 */
	CLUSTER("graphstream/visual_settings_cluster.css");

	private String cssFile;

	GraphView(String cssFile) {
		this.cssFile = cssFile;
	}

	/**
	 * @return the cssFile
	 */
	public String getCssFile() {
		return this.cssFile;
	}
}
