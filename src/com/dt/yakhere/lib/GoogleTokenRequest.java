package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;

public class GoogleTokenRequest extends ConnectionRequest {
	private final String mCode;
	private final String mClientId;
	private final String mClientSecret;

	boolean mIsReady;
	final Map<String, Object> mResponse = new HashMap<String, Object>();
	
    public GoogleTokenRequest(String code, String clientId, String clientSecret) {
    	mCode = code;
    	mClientId = clientId;
    	mClientSecret = clientSecret;
    	
    	mIsReady = false;
    	   	
    	setUrl("https://www.googleapis.com/oauth2/v3/token?code=" + mCode + "&client_id=" + mClientId + "&client_secret=" + mClientSecret + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob:auto&grant_type=authorization_code");
    	setDuplicateSupported(true);
    	setPost(true);
    }
	
	@Override
	protected void postResponse() {

	}
	
	@Override
	protected void readResponse(InputStream input) throws IOException {
    	final Reader inputReader = new InputStreamReader(input);
		final JSONParser parser = new JSONParser();
		final Map<String, Object> responseBody = parser.parseJSON(inputReader);
		mResponse.putAll(responseBody);
		mIsReady = true;
	}
    
    public boolean isReady() {
    	return mIsReady;
    }
    
    public Map<String, Object> getResponse() {
    	return mResponse;
    }
	
	@Override
	protected void handleIOException(IOException err) {
	    super.handleIOException(err);
	}
}
