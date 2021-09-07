package analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class TestSetOperation {

	private List<Set<String>> list;

	@BeforeAll
	public void initialize() throws FileNotFoundException {
		this.list = new ArrayList<>();
		this.list.add(new HashSet<>(Set.of("Test", "String")));
		this.list.add(new HashSet<>(Set.of("Test", "String", "Hello", "Me")));
		this.list.add(new HashSet<>(Set.of("String")));
	}

	@Test
	public void testHandle() {
		Set<String> res = SetOperations.HIGHEST_PRIO.handle(this.list);
		assertEquals(2, res.size());
		res = SetOperations.CONCAT.handle(this.list);
		assertEquals(4, res.size());
	}
}
