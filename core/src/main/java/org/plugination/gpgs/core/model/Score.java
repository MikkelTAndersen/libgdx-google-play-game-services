package org.plugination.gpgs.core.model;

public class Score {
	private Player player;
	private Long scoreValue;
	private Long scoreRank;

	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public void setScoreRank(Long scoreRank) {
		this.scoreRank = scoreRank;
	}
	public Long getScoreRank() {
		return scoreRank;
	}
	public void setScoreValue(Long scoreValue) {
		this.scoreValue = scoreValue;
	}
	public Long getScoreValue() {
		return scoreValue;
	}
}
