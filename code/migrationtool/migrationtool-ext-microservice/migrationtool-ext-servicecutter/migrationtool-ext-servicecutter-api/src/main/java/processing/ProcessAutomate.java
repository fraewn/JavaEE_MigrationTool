package processing;

public interface ProcessAutomate<T extends ProcessAutomate<?>> {

	T nextStep();

	T previousStep();

	String name();

	int ordinal();
}
