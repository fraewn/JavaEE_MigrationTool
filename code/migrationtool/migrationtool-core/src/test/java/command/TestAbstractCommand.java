package command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import command.extension.DefaultCommand;
import exceptions.MigrationToolArgumentException;
import exceptions.MigrationToolInitException;

public class TestAbstractCommand {

	private static Stream<Arguments> provideArgsForCommand() {
		return Stream.of(
				Arguments.of(new String[0], MigrationToolArgumentException.class),
				Arguments.of(new String[] { "-path=Test" }, null),
				Arguments.of(new String[] { "-help" }, MigrationToolArgumentException.class),
				Arguments.of(new String[] { "-model=Unknown" }, MigrationToolInitException.class));
	}

	@ParameterizedTest
	@MethodSource("provideArgsForCommand")
	public void testCommand(String[] args, Class<? extends Throwable> expected) {
		AbstractCommand runnableCommand = new DefaultCommand();
		if (expected != null) {
			assertThrows(expected, () -> runnableCommand.run(args));
		} else {
			runnableCommand.run(args);
		}
	}
}
