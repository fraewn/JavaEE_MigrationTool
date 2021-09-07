package migration.model.serviceDefintion;

/**
 * Defines the direction of the shared language between two services
 */
public enum Direction {

	/**
	 * Service A provides the shared entities which Service B consumes
	 */
	OUTGOING,
	/**
	 * Service B provides the shared entities which Service A consumes
	 */
	INCOMING,
	/**
	 * Both Serivces A and B provides entites which need to be shared with the other
	 * service
	 */
	BIDIRECTIONAL

}
