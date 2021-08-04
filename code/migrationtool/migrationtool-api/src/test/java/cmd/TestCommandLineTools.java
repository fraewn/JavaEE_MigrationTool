package cmd;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineParser;

import command.extension.TestCommand;

public class TestCommandLineTools {

	@Test
	public void testCommandLineParser() {
		TestCommand command = new TestCommand();
		String args[] = { "-model=Test", "-interpreter=Hello" };
		CommandLineParser.parse(args, command);
		assertAll("Should return all options which set by TestCommand",
				() -> assertEquals("Test", command.getModel()),
				() -> assertEquals("Hello", command.getInterpreter()),
				() -> assertNull(command.getLoader()));
	}

	@Test
	public void testCommandLineSplitterDefinedArgs() {
		TestCommand command = new TestCommand();
		String args[] = { "-model=Test", "-interpreter=Hello", "-hello=World" };
		CmdLineParser parser = new CmdLineParser(command);
		String[] result = CommandLineSplitter.definedArgs(args, parser);
		assertAll("Should return all possible options which can be set by TestCommand",
				() -> assertEquals(2, result.length),
				() -> assertEquals("-model=Test", result[0]),
				() -> assertEquals("-interpreter=Hello", result[1]));
	}

	@Test
	public void testCommandLineSplitterUndefinedArgs() {
		TestCommand command = new TestCommand();
		String args[] = { "-model=Test", "-interpreter=Hello", "-hello=World" };
		CmdLineParser parser = new CmdLineParser(command);
		String[] result = CommandLineSplitter.undefinedArgs(args, parser);
		assertAll("Should return all possible options which can not be set by TestCommand",
				() -> assertEquals(1, result.length),
				() -> assertEquals("-hello=World", result[0]));
	}
}
