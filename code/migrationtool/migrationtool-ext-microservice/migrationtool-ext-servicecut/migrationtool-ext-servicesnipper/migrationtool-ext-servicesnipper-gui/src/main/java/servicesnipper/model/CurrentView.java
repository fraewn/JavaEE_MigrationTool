package servicesnipper.model;

public enum CurrentView {
	/**
	 *
	 */
	MODEL("model_config", "model_visual"),
	/**
	 *
	 */
	GRAPH("graph_config", "graph_visual");

	private String locationSideMenu;

	private String locationContent;

	CurrentView(String locationSideMenu, String locationContent) {
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
