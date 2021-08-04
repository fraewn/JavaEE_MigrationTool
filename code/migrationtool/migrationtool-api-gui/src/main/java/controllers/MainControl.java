package controllers;

/**
 * Methods to orchestrate the main events from the migrationtool
 *
 * @param <E> processing enum
 */
public interface MainControl<E extends Enum<E>> {

	/**
	 * Call method after initialization of fxml
	 *
	 * @param <T> dto object
	 * @param dto object
	 */
	<T> void afterInitialize(T dto);

	/**
	 * Show the current step name and the overall progress
	 *
	 * @param label   String text
	 * @param current
	 */
	void setProgress(String label, int current);

	/**
	 * Pass a argument before the visualization method gets triggered
	 *
	 * @param <T> dto
	 * @param dto object
	 */
	<T> void beforeVisualize(T dto);

	/**
	 * Visualize the passed dto object
	 *
	 * @param <T>  dto
	 * @param dto  object
	 * @param step step
	 */
	<T> void visualize(T dto, E step);

	/**
	 * Gets the overall state of the gui
	 *
	 * @param <T> dto
	 * @return dto object
	 */
	<T> T getModel();

	/**
	 * Migrationtool is waiting for an user action
	 *
	 * @param step processing step
	 */
	void awaitApproval(E step);

	/**
	 * Checks if the defined processing step is already approved
	 *
	 * @param step processing step
	 * @return processing approved
	 */
	boolean isApproved(E step);

	/**
	 * Checks if the defined processing is accepted
	 *
	 * @param step processing step
	 * @return processing undo
	 */
	boolean isUndo(E step);
}
