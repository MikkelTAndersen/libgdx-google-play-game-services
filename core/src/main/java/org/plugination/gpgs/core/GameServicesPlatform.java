package org.plugination.gpgs.core;

import java.util.List;

import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.RankType;
import org.plugination.gpgs.core.model.Score;
import org.plugination.gpgs.core.model.TimeSpan;

public interface GameServicesPlatform {

	void getPlayer(String playerId, Response<Player> callback);

    void submitScore(String leaderboardId, Long score, boolean increment);
    void getScore(String leaderboardId, RankType rankType, TimeSpan timeSpan, Response<Score[]> callback);

    void unlockAchievement(String achievementID);
    void getAchievements(String id, Response<List<String>> callback);

    public interface Response<T> {
    	void onSuccess(T data);
    	void onError(Exception e);
    }

    public interface Customizer {
    	/**
    	 * The client id without .apps.googleusercontent.com
    	 */
    	String getClientID();
    	String getClientSecret();
    	String getScope();
    }

}
