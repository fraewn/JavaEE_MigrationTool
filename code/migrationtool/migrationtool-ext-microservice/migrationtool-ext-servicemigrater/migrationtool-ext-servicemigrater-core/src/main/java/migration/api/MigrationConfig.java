package migration.api;

import migration.Converter;
import migration.spring.SpringConverter;
import migration.spring.SpringFactory;

public enum MigrationConfig {

	SPRING(new SpringConverter(new SpringFactory()));

	private Converter converter;

	MigrationConfig(Converter converter) {
		this.converter = converter;
	}

	/**
	 * @return the converter
	 */
	public Converter getConverter() {
		return converter;
	}

}
