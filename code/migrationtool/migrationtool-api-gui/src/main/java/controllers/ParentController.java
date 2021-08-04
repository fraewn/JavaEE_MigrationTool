package controllers;

/**
 * Create a simple parent => child relationship between controllers
 *
 * @param <E> Processing Enum
 */
public interface ParentController<E extends Enum<E>> {

	/**
	 * Approve/Decline the process of the current step
	 *
	 * @param step step
	 * @param undo undo step
	 */
	void approve(E step, boolean undo);

	/**
	 * Fire a specific event from the child controller
	 */
	void callbackEvent();

	/**
	 * Fire a specific event from the child controller
	 */
	void callbackEvent(ChildController<E> caller);
}
