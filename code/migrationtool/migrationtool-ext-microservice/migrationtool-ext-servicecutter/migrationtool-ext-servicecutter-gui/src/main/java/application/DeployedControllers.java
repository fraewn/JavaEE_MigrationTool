package application;

import java.util.HashMap;
import java.util.Map;

import controllers.Controller;

public class DeployedControllers {

	private Map<Class<? extends Controller>, Controller> controllers;

	public DeployedControllers() {
		this.controllers = new HashMap<>();
	}

	public void addController(Class<? extends Controller> c, Controller controller) {
		this.controllers.put(c, controller);
	}

	public <T extends Controller> T getController(Class<? extends T> c) {
		return (T) this.controllers.get(c);
	}
}
