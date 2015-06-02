package org.plugination.gpgs.core;

import java.util.ArrayList;
import java.util.List;

import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.RankType;
import org.plugination.gpgs.core.model.Score;
import org.plugination.gpgs.core.model.TimeSpan;

import android.content.Context;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.Players.LoadPlayersResult;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadScoresResult;
import com.google.android.gms.plus.Plus;

public class AndroidGameServicePlatform implements GameServicesPlatform,
		ConnectionCallbacks, OnConnectionFailedListener {

	public static AndroidGameServicePlatform init(Customizer customizer, Context context) {
		 GameServices.platform = new AndroidGameServicePlatform(customizer, context);
		return (AndroidGameServicePlatform)GameServices.platform;
	}

	public static AndroidGameServicePlatform init(Context context) {
		GameServices.platform = new AndroidGameServicePlatform(
				new DefaultAndroidCustomizer(), context);
		return (AndroidGameServicePlatform)GameServices.platform;
	}

	private GoogleApiClient apiClient;
	private Context context;

	private AndroidGameServicePlatform(final Customizer customizer, Context context) {
		if (customizer == null) {
			throw new IllegalArgumentException("Customizer cannot be null");
		}
		if (context == null) {
			throw new IllegalArgumentException("Context cannot be null");
		}
		this.context = context;
	}

	public void connect() {
		try {
			apiClient = new GoogleApiClient.Builder(context)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
					.addApi(Games.API).addScope(Games.SCOPE_GAMES)
					.build();
		} catch (Exception e) {
			Gdx.app.error("GPGS-error", e.getMessage(), e);
		}
		apiClient.connect();
	}

	@Override
	public void submitScore (final String leaderboardId, final Long score, boolean increment) {
		try {
			if (increment) {
				PendingResult<LoadPlayerScoreResult> oldScore = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(apiClient, leaderboardId, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC);
				oldScore.setResultCallback(new ResultCallback<LoadPlayerScoreResult>() {
					@Override
					public void onResult(LoadPlayerScoreResult result) {
						Long newScore = result.getScore().getRawScore()+ score;
						Games.Leaderboards.submitScore(apiClient, leaderboardId, newScore);
					}
				});
			} else {
				Games.Leaderboards.submitScore(apiClient, leaderboardId, score);
			}
		} catch (Exception e) {
			Gdx.app.error("GPGS-error", e.getMessage(), e);
		}
	}

	@Override
	public void unlockAchievement(String achievementID) {
	}

	@Override
	public void getPlayer(String playerId, final Response<Player> callback) {
		try {
			Games.Players.loadPlayer(apiClient, playerId).setResultCallback(new ResultCallback<Players.LoadPlayersResult>() {
				@Override
				public void onResult(LoadPlayersResult result) {
					if(result.getPlayers().getCount() > 0) {
						com.google.android.gms.games.Player gpgsPlayer = result.getPlayers().get(0);
						Player player = new Player();
						player.setAvatarImageUrl(gpgsPlayer.getIconImageUrl());
						player.setDisplayName(gpgsPlayer.getDisplayName());
						callback.onSuccess(player);
					}
				}
			});
		} catch (Exception e) {
			callback.onError(e);
		}
	}

	@Override
	public void getScore(String leaderboardId, RankType rankType, TimeSpan timeSpan, final Response<Score[]> callback) {
		try {
			PendingResult<LoadScoresResult> loadTopScores = Games.Leaderboards.loadTopScores(apiClient, leaderboardId, getSpan(timeSpan), getLeaderboardCollection(rankType), 5);
			loadTopScores.setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>() {
				@Override
				public void onResult(Leaderboards.LoadScoresResult loaded) {
					LeaderboardScoreBuffer items = loaded.getScores();
					if(items != null) {
						List<Score> result = new ArrayList<Score>();

						for (LeaderboardScore leaderboardScore : items) {
							Player player = new Player();
							player.setAvatarImageUrl(leaderboardScore.getScoreHolderIconImageUrl());
							player.setDisplayName(leaderboardScore.getScoreHolderDisplayName());
							Score score = new Score();
							score.setPlayer(player);
							score.setScoreRank(leaderboardScore.getRank());
							score.setScoreValue(leaderboardScore.getRawScore());
							result.add(score);
						}
						callback.onSuccess(result.toArray(new Score[result.size()]));
					}
				}
			});
		} catch (Exception e) {
			callback.onError(e);
		}
	}

	private int getLeaderboardCollection(RankType rackType) {
		if(RankType.PUBLIC.equals(rackType)) {
			return LeaderboardVariant.COLLECTION_PUBLIC;
		} else if(RankType.SOCIAL.equals(rackType)) {
			return LeaderboardVariant.COLLECTION_SOCIAL;
		} return 0;
	}

	private int getSpan(TimeSpan timeSpan) {
		if(TimeSpan.ALL.equals(timeSpan) || TimeSpan.ALL_TIME.equals(timeSpan)) {
			return LeaderboardVariant.TIME_SPAN_ALL_TIME;
		} else if(TimeSpan.DAILY.equals(timeSpan)) {
			return LeaderboardVariant.TIME_SPAN_DAILY;
		} else if(TimeSpan.WEEKLY.equals(timeSpan)) {
			return LeaderboardVariant.TIME_SPAN_WEEKLY;
		}
		return 0;
	}

	@Override
	public void getAchievements(String id, Response<List<String>> callback) {
	}

	public interface Customizer extends GameServicesPlatform.Customizer {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

	}

	@Override
	public void onConnected(Bundle connectionHint) {

	}

	@Override
	public void onConnectionSuspended(int cause) {

	}
}
