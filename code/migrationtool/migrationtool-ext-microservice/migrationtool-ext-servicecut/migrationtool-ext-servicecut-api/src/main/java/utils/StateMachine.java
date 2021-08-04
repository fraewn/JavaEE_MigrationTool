package utils;

/**
 * Generic class to imitate the process of a simple state machine. The values
 * are connected like the order of there definition.
 *
 * @param <E>
 */
public class StateMachine<E extends Enum<E>> {

	/** all possible values */
	private E[] values;
	/** current value */
	private E current;

	public StateMachine(Class<E> enumType) {
		this.values = enumType.getEnumConstants();
		this.current = startState();
	}

	/**
	 * Gets the start state of the state machine. Enum value with ordinal 0
	 *
	 * @return get start state
	 */
	public E startState() {
		return this.values[0];
	}

	/**
	 * Gets the finished state of the state machine. Enum value with the highest
	 * ordinal
	 *
	 * @return get finished state
	 */
	public E finishedState() {
		return this.values[this.values.length - 1];
	}

	/**
	 * @return processCount = highest ordinal value
	 */
	public int processCount() {
		return this.values.length;
	}

	/**
	 * Sets the state machine to the next state. If the current step is the finished
	 * state, then this method has no effect
	 *
	 * @return next state
	 */
	public E nextStep() {
		this.current = this.values[Math.min(this.current.ordinal() + 1, this.values.length - 1)];
		return this.current;
	}

	/**
	 * Sets the state machine to the previous state. If the current step is the
	 * start state, then this method has no effect
	 *
	 * @return previous state
	 */
	public E previousStep() {
		this.current = this.values[Math.max(this.current.ordinal() - 1, 0)];
		return this.current;
	}

	/**
	 * @return current state equals finished state
	 */
	public boolean isProcessDone() {
		return this.current.equals(finishedState());
	}

	/**
	 * @return progress visualized in a number
	 */
	public int getProcentOfProgress() {
		return getProcentOfProgress(0, 0);
	}

	/**
	 * @param ordinalSubProcess subprocess count
	 * @return progress visualized in a number
	 */
	public int getProcentOfProgress(int ordinalSubProcess, int subProcessCount) {
		double processCount = processCount();
		double ordinal = (this.current.ordinal() + 1) / processCount;
		double res = ordinal;
		if (ordinalSubProcess != 0) {
			double range = 1. / processCount;
			double y = ((double) ordinalSubProcess) / subProcessCount;
			res += y * range;
		}
		return (int) (res * 100);
	}

	/**
	 * @return the current
	 */
	public E getCurrent() {
		return this.current;
	}
}
