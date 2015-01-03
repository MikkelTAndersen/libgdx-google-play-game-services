package org.plugination.gpgs.core.model;

/**
 * The types of ranks to return. If the parameter is omitted, no ranks will be returned.
 *
 * @author mikkelandersen
 */
public enum RankType {
	/**
	 * Retrieve public and social ranks.
	 */
	ALL("ALL"),
	/**
	 * Retrieve public ranks, if the player is sharing their gameplay activity publicly.
	 */
	PUBLIC("PUBLIC"),
	/**
	 * Retrieve the social rank.
	 */
	SOCIAL("SOCIAL");

	private String rankLabel;

	RankType(String rankLabel) {
		this.rankLabel = rankLabel;
	}

	@Override
	public String toString() {
		return rankLabel;
	}
}
