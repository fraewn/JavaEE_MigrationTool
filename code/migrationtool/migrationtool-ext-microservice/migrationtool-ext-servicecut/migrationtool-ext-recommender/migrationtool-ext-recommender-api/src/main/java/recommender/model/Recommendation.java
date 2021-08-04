package recommender.model;

/**
 * Class representing all information of a recommendation
 */
public class Recommendation {
	/** name of the affected instance/usecase */
	private String name;
	/** calculated metric value */
	private int metricValue;
	/** corresponding group */
	private String relatedGroup;
	/** should recommendation be included in final model */
	private boolean included;

	public Recommendation(String name, int metricValue, String relatedGroup, boolean included) {
		this.name = name;
		this.metricValue = metricValue;
		this.relatedGroup = relatedGroup;
		this.included = included;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the metricValue
	 */
	public int getMetricValue() {
		return this.metricValue;
	}

	/**
	 * @param metricValue the metricValue to set
	 */
	public void setMetricValue(int metricValue) {
		this.metricValue = metricValue;
	}

	/**
	 * @return the included
	 */
	public boolean isIncluded() {
		return this.included;
	}

	/**
	 * @param included the included to set
	 */
	public void setIncluded(boolean included) {
		this.included = included;
	}

	/**
	 * @return the relatedGroup
	 */
	public String getRelatedGroup() {
		return this.relatedGroup;
	}

	/**
	 * @param relatedGroup the relatedGroup to set
	 */
	public void setRelatedGroup(String relatedGroup) {
		this.relatedGroup = relatedGroup;
	}

	@Override
	public String toString() {
		return "Recommandation[" + this.name + ", value=" + this.metricValue + " ,group=" + this.relatedGroup
				+ " ,included=" + this.included + "]";
	}
}
