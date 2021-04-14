package service.extension;

import api.LoaderService;

public class One implements LoaderService {
	@Override
	public void execute() {
		System.out.println("Hello World");
	}
}
