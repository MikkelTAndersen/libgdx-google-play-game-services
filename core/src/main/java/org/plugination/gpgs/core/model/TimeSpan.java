package org.plugination.gpgs.core.model;

/**
 * The time span for the scores and ranks you're requesting.
 * @author mikkelandersen
 */
public enum TimeSpan {
	/**
	 * Get the high scores for all time spans. If this is used, maxResults values will be ignored.
	 */
	ALL("ALL"),
	/**
	 * Get the all time high score.
	 */
	ALL_TIME("ALL_TIME"),
	/**
	 * List the top scores for the current day.
	 */
	DAILY("DAILY"),
	/**
	 * List the top scores for the current week.
	 */
	WEEKLY("WEEKLY");

	private String timeSpanLabel;

	TimeSpan(String timeSpanLabel) {
		this.timeSpanLabel = timeSpanLabel;
	}

	@Override
	public String toString() {
		return timeSpanLabel;
	}
}
