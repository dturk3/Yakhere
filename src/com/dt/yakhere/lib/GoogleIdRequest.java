package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;

public class GoogleIdRequest extends ConnectionRequest {
	final String mAuthToken;

	boolean mIsReady;
	final Map<String, Object> mResponse = new HashMap<String, Object>();
	
    public GoogleIdRequest(String authToken) {
    	mAuthToken = authToken;
    	mIsReady = false;
    	
    	setUrl("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + mAuthToken);
    	setDuplicateSupported(true);
    	setPost(false);
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
