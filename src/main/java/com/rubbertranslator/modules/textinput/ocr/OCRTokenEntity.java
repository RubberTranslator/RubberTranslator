package com.rubbertranslator.modules.textinput.ocr;

import com.google.gson.annotations.SerializedName;

public class OCRTokenEntity{

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("refresh_token")
	private String refreshToken;

	@SerializedName("scope")
	private String scope;

	@SerializedName("session_key")
	private String sessionKey;

	@SerializedName("expires_in")
	private int expiresIn;

	@SerializedName("session_secret")
	private String sessionSecret;

	public String getAccessToken(){
		return accessToken;
	}

	public String getRefreshToken(){
		return refreshToken;
	}

	public String getScope(){
		return scope;
	}

	public String getSessionKey(){
		return sessionKey;
	}

	public int getExpiresIn(){
		return expiresIn;
	}

	public String getSessionSecret(){
		return sessionSecret;
	}
}