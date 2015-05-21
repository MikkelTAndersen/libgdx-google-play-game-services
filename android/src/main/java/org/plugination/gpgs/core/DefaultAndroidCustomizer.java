package org.plugination.gpgs.core;

import org.plugination.gpgs.core.AndroidGameServicePlatform.Customizer;


public class DefaultAndroidCustomizer implements Customizer {

	public String getClientID() {
		return "";
	}

	@Override
	public String getClientSecret() {
		return "";
	}

	@Override
	public String getScope() {
		return "https://www.googleapis.com/auth/games";
	}
}