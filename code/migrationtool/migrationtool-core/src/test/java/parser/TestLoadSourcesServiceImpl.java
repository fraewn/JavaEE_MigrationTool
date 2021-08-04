package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestLoadSourcesServiceImpl {

	@Test
	public void testLoadSources() {
		LoadSourcesService service = new LoadSourcesServiceImpl();
		service.loadSources("src/test/resources/source");
		assertEquals(2, service.getAllCompilationUnits().size());
	}
}
