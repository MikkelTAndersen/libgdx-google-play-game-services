
package org.plugination.gpgs.core;

import java.util.List;

import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.RankType;
import org.plugination.gpgs.core.model.Score;
import org.plugination.gpgs.core.model.TimeSpan;

public class DefaultGameServicesPlatform implements GameServicesPlatform {

	@Override
	public void getPlayer (String playerId, Response<Player> callback) {
	}

	@Override
	public void submitScore (String leaderboardId, Long score, boolean increment) {
	}

	@Override
	public void getScore (String leaderboardId, RankType rankType, TimeSpan timeSpan, Response<Score[]> callback) {
	}

	@Override
	public void unlockAchievement (String achievementID) {
	}

	@Override
	public void getAchievements (String id, Response<List<String>> callback) {
	}
}
