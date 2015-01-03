package org.plugination.gpgs.core;


public class GameServices {
	protected static GameServicesPlatform platform;

	private GameServices() {
	}

	public static GameServicesPlatform getPlatform() {
		return platform;
	}
}