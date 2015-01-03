package org.plugination.gpgs.core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import org.plugination.gpgs.core.DesktopGameServicePlatform.Customizer;
import org.plugination.gpgs.core.GameServicesPlatform.Response;

import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class DefaultDesktopCustomizer implements Customizer {
	private String clientId;
	private String clientSecret;

	public DefaultDesktopCustomizer(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	@Override
	public DataStoreFactory getDataStoreFactory() {
		File dataDirectory = new File(System.getProperty("java.io.tmpdir"));
		try {
			return new FileDataStoreFactory(dataDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getRedirectUrl() {
		return "urn:ietf:wg:oauth:2.0:oob";
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

	@Override
	public void showAuthorizationUrl(String authorizationUrl) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(new URI(authorizationUrl));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else {
	    	//HANDLE THIS!?!
	    }
	}

	@Override
	public void getResponse(Response<String> response) {
		String result = (String)JOptionPane.showInputDialog(null, "Please paste code from google below.", "Google Authentication code", JOptionPane.PLAIN_MESSAGE);
		if(result == null || result.isEmpty()) {
			response.onError(new IllegalArgumentException(""));
		} else {
			response.onSuccess(result);
		}
	}
}
