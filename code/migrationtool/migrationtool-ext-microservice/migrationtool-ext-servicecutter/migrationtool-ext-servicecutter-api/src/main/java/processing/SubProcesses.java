package processing;

public interface SubProcesses {

	ProcessAutomate<?> startState();

	ProcessAutomate<?> finishedState();

	int processCount();
}
