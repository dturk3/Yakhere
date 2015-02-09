package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.codename1.io.JSONParser;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkManager;
import com.dt.yakhere.Yakhere;

public class YakhereImageRequest extends MultipartRequest {
	final double mLongitude;
	final double mLatitude;
	final String mPublisher;
	final String mImageFile;

	boolean mIsReady;

	public YakhereImageRequest(double longitude, double latitude, String imageFile, String publisher) {
    	mLongitude = longitude;
    	mLatitude = latitude;
    	mImageFile = imageFile;
    	mPublisher = publisher;
    	mIsReady = false;
    }
    
	@Override
	protected void postResponse() {

	}
	
	@Override
	protected void readResponse(InputStream input) throws IOException {
    	final Reader inputReader = new InputStreamReader(input);
		final JSONParser parser = new JSONParser();
		final Map<String, Object> responseBody = parser.parseJSON(inputReader);
		
		final String imagePath = String.valueOf(responseBody.get("img"));  
		
		final YakhereMessageRequest msgRequest = new YakhereMessageRequest(mLongitude, mLatitude, "img:" + imagePath, mPublisher);
		msgRequest.setUrl(Yakhere.BASE_URL + "/messages");
		msgRequest.setPost(true);
		msgRequest.setContentType("application/json");
		msgRequest.setDuplicateSupported(true);
		NetworkManager.getInstance().addToQueue(msgRequest);

		mIsReady = true;
	}
    
    public boolean isReady() {
    	return mIsReady;
    }
	
	@Override
	protected void handleIOException(IOException err) {
	    super.handleIOException(err);
	}
}
