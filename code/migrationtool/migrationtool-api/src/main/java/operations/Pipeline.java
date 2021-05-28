package operations;

public class Pipeline<I, O> {

	private final ProcessingStep<I, O> currentHandler;

	public Pipeline() {
		this.currentHandler = new DummyStep();
	}

	public Pipeline(ProcessingStep<I, O> currentHandler) {
		this.currentHandler = currentHandler;
	}

	public <K> Pipeline<I, K> addHandler(ProcessingStep<O, K> newHandler) {
		return new Pipeline<>(input -> newHandler.process(this.currentHandler.process(input)));
	}

	public O execute(I input) {
		return this.currentHandler.process(input);
	}

	class DummyStep implements ProcessingStep<I, O> {
		@Override
		public O process(I input) {
			return null;
		}
	}
}
