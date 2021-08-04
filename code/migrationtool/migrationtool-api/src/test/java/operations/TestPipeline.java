package operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TestPipeline {

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testPipeline() {
		Pipeline pipeline = new Pipeline<>();
		pipeline = pipeline.addHandler(new TestLoader());
		pipeline = pipeline.addHandler(new TestModel());
		pipeline = pipeline.addHandler(new TestInterpreter());
		Boolean res = (Boolean) pipeline.execute(null);
		assertEquals(true, res);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testPipelineWrongOrder() {
		Pipeline pipeline = new Pipeline<>();
		pipeline = pipeline.addHandler(new TestModel());
		pipeline = pipeline.addHandler(new TestLoader());
		pipeline = pipeline.addHandler(new TestInterpreter());
		final Pipeline lamda = pipeline;
		assertThrows(ClassCastException.class, () -> lamda.execute(null));
	}

	class TestLoader extends LoaderService<Object, String> {
		@Override
		public String loadProject(Object input) {
			return "Test";
		}
	}

	class TestModel extends ModelService<String, Integer> {

		@Override
		public Integer save(String input) {
			return 1;
		}
	}

	class TestInterpreter extends InterpreterService<Integer, Boolean> {
		@Override
		public Boolean run(Integer input) {
			return true;
		}
	}

}
