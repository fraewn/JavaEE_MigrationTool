package model.erm;

/**
 * Relationship type in object oriented programming
 */
public enum RelationType {
	/**
	 * Aggregation is a one way association.
	 */
	AGGREGATION,
	/**
	 * use of instance variables that are references to other objects
	 */
	COMPOSITION,
	/**
	 * Inheritance is a parent-child relationship where we create a new class by
	 * using existing class code
	 */
	INHERITANCE;
}
