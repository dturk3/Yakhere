package com.dt.yakhere.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;

public class YakhereFeedRequest extends ConnectionRequest {
	final double mLongitude;
	final double mLatitude;

	boolean mIsReady;
	final List<Map<String, String>> mResponse = new ArrayList<Map<String, String>>();
	
    public YakhereFeedRequest(double longitude, double latitude) {
    	mLongitude = longitude;
    	mLatitude = latitude;
    	mIsReady = false;
    }
	
	@Override
	protected void postResponse() {

	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void readResponse(InputStream input) throws IOException {
    	final Reader inputReader = new InputStreamReader(input);
		final JSONParser parser = new JSONParser();
		final Map<String, Object> responseBody = parser.parseJSON(inputReader);
		for (String msg : (List<String>)responseBody.get("feed")) {
			final Map<String, Object> rawMsg = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(msg.getBytes())));
			final Map<String, String> feedMsg = new HashMap<String, String>();
			feedMsg.put("fuzzyTimestamp", String.valueOf(rawMsg.get("fuzzyTimestamp")));
			feedMsg.put("publisher", String.valueOf(rawMsg.get("publisher")));
			feedMsg.put("message", String.valueOf(rawMsg.get("message")));
			feedMsg.put("timestamp", String.valueOf(rawMsg.get("timestamp")));
			mResponse.add(feedMsg);
		}
		mIsReady = true;
	}
    
    public boolean isReady() {
    	return mIsReady;
    }
    
    public List<Map<String, String>> getResponse() {
    	return mResponse;
    }
	 
	@Override
	protected void buildRequestBody(OutputStream os) throws IOException {
		final String jsonRequest = "{\"lon\": \"" + mLongitude + "\", \"lat\": \"" + mLatitude + "\"}";
		os.write(jsonRequest.getBytes());
	}
	
	@Override
	protected void handleIOException(IOException err) {
	    super.handleIOException(err);
	}
}
