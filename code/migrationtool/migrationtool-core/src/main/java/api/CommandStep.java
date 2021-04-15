package api;

public interface CommandStep {
	void setCommandLineArguments(String[] args);

	void execute();
}
