package visualization;

public interface VisualizationService<E extends Enum<E>> {

	/**
	 * Sets the progessbar to the defined number
	 *
	 * @param label    text
	 * @param progress count
	 */
	void setProgress(String label, int progress);

	/**
	 * Awaits a user interaction to apply the current step
	 *
	 * @param step current step
	 * @return apply or decline
	 */
	boolean awaitApproval(E step);

	/**
	 * Reverts a user interaction from the last step
	 *
	 * @param step step
	 */
	void undoStep(E step);

	/**
	 * Stops the gui
	 */
	void stop();
}
