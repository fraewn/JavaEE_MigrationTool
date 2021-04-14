package command;

import java.util.SortedMap;
import java.util.TreeMap;

import api.CommandStep;
import api.LoaderService;

public class TestCommand extends AbstractCommand {

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	protected SortedMap<Class<? extends CommandStep>, String> defineSteps() {
		SortedMap<Class<? extends CommandStep>, String> steps = new TreeMap<>();
		steps.put(LoaderService.class, "one");
		return steps;
	}
}
