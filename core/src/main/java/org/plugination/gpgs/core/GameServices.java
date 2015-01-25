package org.plugination.gpgs.core;


public class GameServices {
	protected static GameServicesPlatform platform;

	private GameServices() {
	}

	public static GameServicesPlatform getPlatform() {
		if(platform == null) {
			platform = new DefaultGameServicesPlatform();
		}
		return platform;
	}
}