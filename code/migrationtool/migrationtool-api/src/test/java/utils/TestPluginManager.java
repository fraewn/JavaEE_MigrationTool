package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import command.extension.TestCommand;
import operations.CommandExtension;
import operations.ProcessingStep;

public class TestPluginManager {

	@Test
	public void testPluginManager() {
		List<CommandExtension> list = PluginManager.findPluginCommands(CommandExtension.class);
		assertEquals(1, list.size());
		assertEquals(TestCommand.class, list.get(0).getClass());
		List<ProcessingStep<?, ?>> list2 = PluginManager.findPluginServices();
		assertEquals(0, list2.size());
	}
}
