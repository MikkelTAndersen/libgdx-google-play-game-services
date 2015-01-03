package org.plugination.gpgs.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.plugination.gpgs.core.GameServices;
import org.plugination.gpgs.core.GameServicesPlatform;
import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.RankType;
import org.plugination.gpgs.core.model.Score;
import org.plugination.gpgs.core.model.TimeSpan;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.games.Games;
import com.google.api.services.games.model.LeaderboardEntry;
import com.google.api.services.games.model.LeaderboardScores;
import com.google.api.services.games.model.PlayerLeaderboardScoreListResponse;

public class DesktopGameServicePlatform implements GameServicesPlatform{
	public static void init(Customizer customizer) {
		GameServices.platform = new DesktopGameServicePlatform(customizer);
	}

	public static void init(String clientId, String clientSecret) {
		GameServices.platform = new DesktopGameServicePlatform(new DefaultDesktopCustomizer(clientId, clientSecret));
	}

	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private GoogleAuthorizationCodeFlow flow;
	private Credential credentials;
	private Games gameAPI;

	private DesktopGameServicePlatform(final Customizer customizer) {
		if(customizer == null) {
			throw new IllegalArgumentException("Customizer cannot be null");
		}
		try {
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, customizer.getClientID()+".apps.googleusercontent.com", customizer.getClientSecret(), Collections.singleton(customizer.getScope())).setDataStoreFactory(customizer.getDataStoreFactory()).build();
			try {
				Credential loadedCredentials = flow.loadCredential("user");
				if(loadedCredentials != null) {
					credentials = loadedCredentials;
					gameAPI = new Games.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials).build();
				} else {
					final String redirectUrl = customizer.getRedirectUrl();
				    String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUrl).build();
				    customizer.showAuthorizationUrl(authorizationUrl);
				    customizer.getResponse(new Response<String>() {
						@Override
						public void onSuccess(String code) {
							try {
								 GoogleTokenResponse googleTokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUrl).execute();
									String token = googleTokenResponse.getRefreshToken();
									try {
										flow.createAndStoreCredential(googleTokenResponse, "user");
									} catch (IOException e) {
										e.printStackTrace();
									}
									credentials = new GoogleCredential.Builder()
							        .setTransport(httpTransport)
							        .setJsonFactory(jsonFactory)
							        .setClientSecrets(customizer.getClientID()+".apps.googleusercontent.com", customizer.getClientSecret())
							        .build().setRefreshToken(token);
									gameAPI = new Games.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials).build();
							} catch (IOException e) {
							}
						}

						@Override
						public void onError(Exception e) {
							e.printStackTrace();
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void submitScore(String leaderboardId, Long score, boolean increment) {
		try {
			Long newScore = score;
			if(increment) {
				PlayerLeaderboardScoreListResponse scores = gameAPI.scores().get("me", leaderboardId, "ALL").execute();
				if(!scores.getItems().isEmpty()) {
					Long oldValue = scores.getItems().get(0).getScoreValue();
					newScore = oldValue + score;
				}
			}
			gameAPI.scores().submit(leaderboardId, newScore).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unlockAchievement(String achievementID) {
	}

	@Override
	public void getPlayer(String playerId, Response<Player> callback) {
		try {
			com.google.api.services.games.model.Player loadedPlayer = gameAPI.players().get(playerId).execute();
			Player player = new Player();
			player.setAvatarImageUrl(loadedPlayer.getAvatarImageUrl());
			player.setDisplayName(loadedPlayer.getDisplayName());
			callback.onSuccess(player);
		} catch (IOException e) {
			callback.onError(e);
		}
	}

	@Override
	public void getScore(String leaderboardId, RankType rankType, TimeSpan timeSpan, Response<Score[]> callback) {
		try {
			LeaderboardScores leaderboardScores = gameAPI.scores().list(leaderboardId, rankType.toString(), timeSpan.toString()).execute();
			List<LeaderboardEntry> items = leaderboardScores.getItems();
			List<Score> result = new ArrayList<Score>();
			for (LeaderboardEntry leaderboardEntry : items) {
				Player player = new Player();
				player.setAvatarImageUrl(leaderboardEntry.getPlayer().getAvatarImageUrl());
				player.setDisplayName(leaderboardEntry.getPlayer().getDisplayName());
				Score score = new Score();
				score.setPlayer(player);
				score.setScoreRank(leaderboardEntry.getScoreRank());
				score.setScoreValue(leaderboardEntry.getScoreValue());
				result.add(score);
			}
			callback.onSuccess(result.toArray(new Score[result.size()]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getAchievements(String id, Response<List<String>> callback) {
	}

	public interface Customizer extends GameServicesPlatform.Customizer {
		DataStoreFactory getDataStoreFactory();

		void getResponse(Response<String> response);

		void showAuthorizationUrl(String authorizationUrl);

		String getRedirectUrl();
	}
}
