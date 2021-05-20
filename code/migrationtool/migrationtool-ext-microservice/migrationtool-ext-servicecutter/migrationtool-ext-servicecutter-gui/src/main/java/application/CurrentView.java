package application;

public enum CurrentView {
	/**
	 *
	 */
	MODEL("config_file", "visual_file"),
	/**
	 *
	 */
	GRAPH("config_graph", "visual_graph");

	private String locationSideMenu;

	private String locationContent;

	private CurrentView(String locationSideMenu, String locationContent) {
		this.locationSideMenu = "gui/fxml/" + locationSideMenu + ".fxml";
		this.locationContent = "gui/fxml/" + locationContent + ".fxml";
	}

	/**
	 * @return the locationSideMenu
	 */
	public String getLocationSideMenu() {
		return this.locationSideMenu;
	}

	/**
	 * @return the locationContent
	 */
	public String getLocationContent() {
		return this.locationContent;
	}
}
