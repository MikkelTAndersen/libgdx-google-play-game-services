package org.plugination.gpgs.core;

import java.util.List;

import org.plugination.gpgs.core.model.Player;
import org.plugination.gpgs.core.model.PlayerRest;
import org.plugination.gpgs.core.model.RankType;
import org.plugination.gpgs.core.model.Score;
import org.plugination.gpgs.core.model.ScoresRest;
import org.plugination.gpgs.core.model.TimeSpan;

import com.google.gwt.core.client.ScriptInjector;

public class HtmlGameServicePlatform implements GameServicesPlatform {
	public static void init(Customizer customizer) {
		GameServices.platform = new HtmlGameServicePlatform(customizer);
	}

	public static void init(String clientId, String clientSecret) {
		GameServices.platform = new HtmlGameServicePlatform(new DefaultHtmlCustomizer(clientId, clientSecret));
	}

	public HtmlGameServicePlatform(Customizer customizer) {
		exposeCallback();
		if(!GwtUtils.hasGoogleAPI()) {
			GwtUtils.addMetaTag("google-signin-clientid", customizer.getClientID()+".apps.googleusercontent.com");
			GwtUtils.addMetaTag("google-signin-cookiepolicy", "single_host_origin");
			GwtUtils.addMetaTag("google-signin-callback", "defaultSigninCallback");
			GwtUtils.addMetaTag("google-signin-scope", customizer.getScope());
			ScriptInjector.fromUrl("https://apis.google.com/js/client.js").inject();
			signIn(customizer.getClientID()+".apps.googleusercontent.com", customizer.getScope());
		}
	}

	private native boolean signIn(String clientId, String scopes) /*-{
	    $wnd.gapi.auth.authorize({client_id: clientId, scope: scopes, immediate: false}, $wnd.defaultCheckSessionStateCallback);
	}-*/;

	private native void exposeCallback() /*-{
	    $wnd.defaultCheckSessionStateCallback = function(isSignedIn){
			if (isSignedIn == true) {
				@org.plugination.gpgs.core.HtmlGameServicePlatform::callbackSignedIn(*)();
			} else {
				@org.plugination.gpgs.core.HtmlGameServicePlatform::callbackSignInFailed(*)();
			}
	    }
	}-*/;

	private static native void consoleLog( String message) /*-{
	    console.log( "GPGS:" + message );
	}-*/;

	public static void callbackSignedIn() {
	}

	public static void callbackSignInFailed() {
	}

	@Override
	public void getPlayer(String playerId, Response<Player> callback) {
		new PlayerRest().getPlayer(playerId, callback);
	}

	@Override
	public void submitScore(String leaderboardId, Long score, boolean increment) {
		new ScoresRest().submitScore(leaderboardId, score.intValue(), increment);
	}

	@Override
	public void getScore(String leaderboardId, RankType rankType, TimeSpan timeSpan, Response<Score[]> callback) {
		new ScoresRest().getScore(leaderboardId, rankType.toString(), timeSpan.toString(), callback);
	}

	@Override
	public void unlockAchievement(String achievementID) {
	}

	@Override
	public void getAchievements(String id, Response<List<String>> callback) {
	}
}