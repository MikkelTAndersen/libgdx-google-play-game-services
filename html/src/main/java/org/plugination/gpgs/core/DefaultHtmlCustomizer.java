package org.plugination.gpgs.core;

import org.plugination.gpgs.core.GameServicesPlatform.Customizer;

public class DefaultHtmlCustomizer implements Customizer {
	private String clientId;
	private String clientSecret;

	public DefaultHtmlCustomizer(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	@Override
	public String getClientID() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getScope() {
		return "https://www.googleapis.com/auth/games";
	}
}
