package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;

public class GoogleGeocodingRequest extends ConnectionRequest {
	private final double mLongitude;
	private final double mLatitude;
	private final String mApiKey;
	private String mResponse = "";

	boolean mIsReady;
	
    public GoogleGeocodingRequest(double longitude, double latitude, String apiKey) {
    	mLongitude = longitude;
    	mLatitude = latitude;
    	mApiKey = apiKey;
    	mIsReady = false;
    	setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mLatitude + "," + mLongitude);
    	setPost(false);
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
		final List<Map<String, Object>> results = (List<Map<String, Object>>)responseBody.get("results");
		final List<Map<String, Object>> addressComponents = (List<Map<String, Object>>)results.get(0).get("address_components");
		final String locality = getByType(addressComponents, "political");
		mResponse = String.valueOf(locality);
		mIsReady = true;
	}

	private String getByType(final List<Map<String, Object>> addressComponents, String type) {
		for (Map<String, Object> component : addressComponents) {
			@SuppressWarnings("unchecked")
			final List<String> types = ((List<String>)component.get("types"));
			if (types.contains(type)) {
				return String.valueOf(component.get("short_name"));
			}
		}
		return "Unknown";
	}
    
    public boolean isReady() {
    	return mIsReady;
    }
    
    
    
    public String getResponse() {
    	return mResponse;
    }
    
	@Override
	protected void handleIOException(IOException err) {
	    super.handleIOException(err);
	}
}
