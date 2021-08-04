package controllers;

/**
 * Create a simple parent => child relationship between controllers
 *
 * @param <E> Processing Enum
 */
public interface ChildController<E extends Enum<E>> {

	/**
	 * Pass a argument after the initialization method gets triggered
	 *
	 * @param <T> dto
	 * @param dto object
	 */
	default <T> void afterInitialization(T dto) {

	}

	/**
	 * Set the parent controller
	 *
	 * @param parent parent controller
	 */
	void setParentController(ParentController<E> parent);

	/**
	 * Informs this controller about the current step
	 *
	 * @param step step
	 */
	void reachedProcessStep(E step);

	/**
	 * Pass a argument before the refresh method gets triggered
	 *
	 * @param <T> dto
	 * @param dto object
	 */
	default <T> void beforeRefreshModel(T dto) {

	}

	/**
	 * Updates the model with the current value
	 *
	 * @param <T> DTO Object
	 * @param dto object
	 */
	<T> void refreshModel(T dto);

	/**
	 * Gets the current model of the child controller
	 *
	 * @param <T> DTO Object
	 * @return model of child
	 */
	<T> T getModel();
}
