package operations;

/**
 * Implementation of the Pipeline pattern
 *
 * @param <I> Input Object
 * @param <O> Output Object
 */
public class Pipeline<I, O> {

	/** pointer to current Handler */
	private final ProcessingStep<I, O> currentHandler;

	public Pipeline() {
		this.currentHandler = new DummyStep();
	}

	public Pipeline(ProcessingStep<I, O> currentHandler) {
		this.currentHandler = currentHandler;
	}

	/**
	 * Adds a step to the current pipeline. This does not execute the process.
	 *
	 * @param <K>        new Output Object
	 * @param newHandler new step
	 * @return new Pipeline object
	 */
	public <K> Pipeline<I, K> addHandler(ProcessingStep<O, K> newHandler) {
		return new Pipeline<>(input -> newHandler.process(this.currentHandler.process(input)));
	}

	/**
	 * Returns the result after applying all steps in the pipeline
	 *
	 * @param input Input Object
	 * @return Output Object
	 */
	public O execute(I input) {
		return this.currentHandler.process(input);
	}

	/**
	 * Creates a dummy step if no input is given
	 */
	class DummyStep implements ProcessingStep<I, O> {
		@Override
		public O process(I input) {
			return null;
		}
	}
}
