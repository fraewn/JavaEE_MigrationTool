package controllers;

import data.GenericDTO;

public interface Controller {

	void initialize() throws Exception;

	void update(GenericDTO<?> dto) throws Exception;
}
