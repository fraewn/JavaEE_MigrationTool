package command.extension;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import operations.CommandExtension;

@Nonnull
public class TestCommand extends CommandExtension {

	private String test;

	private List<String> objects;

	/**
	 * @return the loader
	 */
	public String getLoader() {
		return this.loader;
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(String loader) {
		this.loader = loader;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return this.model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the interpreter
	 */
	@Nullable
	public String getInterpreter() {
		return this.interpreter;
	}

	/**
	 * @param interpreter the interpreter to set
	 */
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void defineSteps(List<String> definedSteps) {

	}

	@Override
	public void beforeInitialization() {
		if (this.objects == null) {
			Object o = new Object();
			o.toString();
			String.valueOf(o);
			this.test = "Helo";
			this.objects = new ArrayList<>();
			new Thread(() -> {
				System.out.println(this.test);
			});
			// huhu
			/* test */
			// its me
		}

	}

	@Override
	public void afterInitialization() {

	}

	@Override
	public void afterExecution() {

	}
}
