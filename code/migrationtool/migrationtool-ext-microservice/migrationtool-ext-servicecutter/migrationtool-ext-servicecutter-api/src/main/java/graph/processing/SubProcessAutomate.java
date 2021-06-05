package graph.processing;

public interface SubProcessAutomate {

	ProcessAutomate<?> startState();

	ProcessAutomate<?> finishedState();

	int processCount();
}
