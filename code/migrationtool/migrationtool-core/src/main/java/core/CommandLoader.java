package core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import command.AbstractCommand;
import command.TestCommand;

public class CommandLoader {

	private static List<Class<? extends AbstractCommand>> availableCommands;

	private static final Logger LOG = Logger.getLogger(CommandLoader.class);

	static {
		availableCommands = new ArrayList<>();
		availableCommands.add(TestCommand.class);
	}

	public static AbstractCommand get(String command) {
		for (Class<? extends AbstractCommand> c : availableCommands) {
			if (c.getSimpleName().equals(command)) {
				try {
					AbstractCommand res = c.getDeclaredConstructor().newInstance();
					return res;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					LOG.error(e.getMessage());
				}
			}
		}
		return null;
	}
}
