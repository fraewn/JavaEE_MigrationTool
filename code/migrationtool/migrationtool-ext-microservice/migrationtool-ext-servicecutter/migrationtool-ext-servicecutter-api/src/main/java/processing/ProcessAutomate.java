package processing;

public interface ProcessAutomate<T extends ProcessAutomate<?>> {

	T nextStep();

	String name();

	int ordinal();
}
