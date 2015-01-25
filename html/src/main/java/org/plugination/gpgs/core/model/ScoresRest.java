package org.plugination.gpgs.core.model;

import java.util.ArrayList;
import java.util.List;

import org.plugination.gpgs.core.GameServicesPlatform.Response;
import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.Score;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class ScoresRest {
	 private Response<Score[]> responseCallback;

	public void getScore(String leaderboardId, String collection, String timeSpan, Response<Score[]> responseCallback) {
		this.responseCallback = responseCallback;
		getScoreJS(leaderboardId, collection, timeSpan);
	}

	/**
	 * @see https://developers.google.com/games/services/web/api/scores
	 * @param leaderboardId
	 * @param collection
	 * @param timeSpan
	 * @param responseCallback
	 * @return
	 */
	private native boolean getScoreJS(String leaderboardId, String collection, String timeSpan) /*-{
   		var instance = this;
   		$wnd.gapi.client.request({
		      path: '/games/v1/leaderboards/'+leaderboardId+'/scores/'+collection,
		      params: {leaderboardId: leaderboardId,
		      		   collection: collection,
		      		   timeSpan: timeSpan},
		      method: 'get',
		      callback: function(response) {
		      	instance.@org.plugination.gpgs.core.model.ScoresRest::handleGetScore(Lcom/google/gwt/core/client/JavaScriptObject;)(response);
		      }
		    });
	}-*/;

	public void handleGetScore(JavaScriptObject response) {
		try {
			List<Score> result = new ArrayList<Score>();
			JSONObject obj = new JSONObject(response);
			JSONValue jsonValue = obj.get("items");
			if(jsonValue != null ) {
				JSONArray items = jsonValue.isArray();
				for (int i = 0; i < items.size(); i++) {
					JSONValue scoreData = items.get(i);
					Score score = new Score();
					Player player = new Player();
					JSONValue playerData = scoreData.isObject().get("player");
					player.setDisplayName(playerData.isObject().get("displayName").toString().replaceAll("\"", ""));
					player.setAvatarImageUrl(playerData.isObject().get("avatarImageUrl").toString().replaceAll("\"", ""));
					score.setPlayer(player);
					score.setScoreRank(new Long(scoreData.isObject().get("scoreRank").toString().replaceAll("\"", "")));
					score.setScoreValue(new Long(scoreData.isObject().get("scoreValue").toString().replaceAll("\"", "")));
					result.add(score);
				}
			}
			responseCallback.onSuccess(result.toArray(new Score[result.size()]));
		} catch (Exception e) {
			responseCallback.onError(e);
		}
	}

	public native void submitScore(String leaderboardId, int score, boolean increment) /*-{
		if(increment) {
			var collection = 'PUBLIC';
			var timeSpan = 'ALL_TIME';
			$wnd.gapi.client.request({
			      path: '/games/v1/leaderboards/'+leaderboardId+'/scores/'+collection,
			      params: {leaderboardId: leaderboardId,
			      		   collection: collection,
			      		   timeSpan: timeSpan},
			      method: 'get',
			      callback: function(response) {
			      	console.log(response);
			      	var oldScore = parseInt(response.numScores) > 0  ? parseInt(response.playerScore.scoreValue) : 0;
			      	var newScore = oldScore + score;
			      	console.log(newScore);
			      	console.log(oldScore);
			      	console.log(score);
			      	$wnd.gapi.client.request({
				      path: '/games/v1/leaderboards/'+leaderboardId+'/scores',
				      params: {leaderboardId: leaderboardId,
				      		   score: newScore},
				      method: 'post',
				      callback: function(response) {
				      }
				    });
			      }
			    });
		} else {
			$wnd.gapi.client.request({
			      path: '/games/v1/leaderboards/'+leaderboardId+'/scores',
			      params: {leaderboardId: leaderboardId,
			      		   score: score},
			      method: 'post',
			      callback: function(response) {
			      }
			    });
	    }
	}-*/;
}
