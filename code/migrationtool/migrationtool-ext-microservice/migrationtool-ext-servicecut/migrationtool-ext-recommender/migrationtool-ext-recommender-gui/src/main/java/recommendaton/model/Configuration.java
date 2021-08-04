package recommendaton.model;

import java.util.ArrayList;
import java.util.List;

import recommender.model.Recommendation;

public class Configuration {

	private List<Recommendation> recommendations;

	public Configuration() {
		this.recommendations = new ArrayList<>();
	}

	/**
	 * @return the recommendations
	 */
	public List<Recommendation> getRecommendations() {
		return this.recommendations;
	}

	/**
	 * @param recommendations the recommendations to set
	 */
	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

}
