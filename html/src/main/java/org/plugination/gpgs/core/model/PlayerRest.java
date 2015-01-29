package org.plugination.gpgs.core.model;

import org.plugination.gpgs.core.GameServicesPlatform.Response;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class PlayerRest {
	 private Response<Player> responseCallback;

	public void getPlayer(String playerId, Response<Player> responseCallback) {
		this.responseCallback = responseCallback;
		getPlayerJS(playerId);
	}

	/**
	 * @see https://developers.google.com/games/services/web/api/index#Players
	 * @param playerId
	 * @param responseCallback
	 * @return
	 */
	private native void getPlayerJS(String playerId) /*-{
   		var instance = this;

   		$wnd.gapi.client.request({
		      path: '/games/v1/players/'+playerId,
		      method: 'get',
		      callback: function(response) {
		      	instance.@org.plugination.gpgs.core.model.PlayerRest::handleGetPlayer(Lcom/google/gwt/core/client/JavaScriptObject;)(response);
		      }
		    });
	}-*/;

	public void handleGetPlayer(JavaScriptObject response) {
		try {
			JSONObject playerData = new JSONObject(response);
			Player player = new Player();
			player.setDisplayName(playerData.isObject().get("displayName").toString().replaceAll("\"", ""));
			player.setAvatarImageUrl(playerData.isObject().get("avatarImageUrl").toString().replaceAll("\"", ""));
			responseCallback.onSuccess(player);
		} catch (Exception e) {
			responseCallback.onError(e);
		}
	}
}
