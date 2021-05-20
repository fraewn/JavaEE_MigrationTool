package core;

import static resolver.Scores.MAX_SCORE;
import static resolver.Scores.MIN_SCORE;

import resolver.Scores;

public class EdgeWeight {

	private double score;

	private double priority;

	public EdgeWeight(double score, double priority) {
		setScore(score);
		this.priority = priority;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		if ((score < Scores.getScore(MIN_SCORE)) || (score > Scores.getScore(MAX_SCORE))) {
			throw new IllegalArgumentException("score should be between -10 and 10");
		}
		this.score = score;
	}

	/**
	 * @return the priority
	 */
	public double getPriority() {
		return this.priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(double priority) {
		this.priority = priority;
	}
}
