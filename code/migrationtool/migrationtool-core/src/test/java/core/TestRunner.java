package core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public class TestRunner {

	private static Stream<Arguments> provideArgsForRunner() {
		return Stream.of(
				Arguments.of(new String[0], true),
				Arguments.of(new String[] { "-path=Test" }, false),
				Arguments.of(new String[] { "-help" }, false));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForRunner")
	public void testRunner(String[] args, boolean expectedError) {
		Runner run = new Runner();
		boolean error = run.run(args);
		assertEquals(expectedError, error);
	}
}
