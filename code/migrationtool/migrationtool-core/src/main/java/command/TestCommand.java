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
	protected SortedMap<String, Class<? extends CommandStep>> defineSteps() {
		SortedMap<String, Class<? extends CommandStep>> steps = new TreeMap<>();
		steps.put("One", LoaderService.class);
		return steps;
	}

	@Override
	protected void setCommandLineArguments(String[] args) {

	}
}
