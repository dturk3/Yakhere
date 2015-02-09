package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import com.codename1.io.ConnectionRequest;
import com.codename1.processing.Result;

public class YakhereMessageRequest extends ConnectionRequest {
	final double mLongitude;
	final double mLatitude;
	final String mMsg;
	final String mPublisher;

	boolean mIsReady;
	
    public YakhereMessageRequest(double longitude, double latitude, String msg, String publisher) {
    	mLongitude = longitude;
    	mLatitude = latitude;
    	mMsg = msg;
    	mPublisher = publisher;
    	mIsReady = false;
    }
    
	@Override
	protected void postResponse() {

	}
	
	@Override
	protected void readResponse(InputStream input) throws IOException {
		mIsReady = true;
	}
    
    public boolean isReady() {
    	return mIsReady;
    }
    
	@Override
	protected void buildRequestBody(OutputStream os) throws IOException {
		final Hashtable<String, String> requestTable = new Hashtable<String, String>();
		requestTable.put("lon", String.valueOf(mLongitude));
		requestTable.put("lat", String.valueOf(mLatitude));
		requestTable.put("message", String.valueOf(mMsg));
		requestTable.put("publisher", String.valueOf(mPublisher));
		final String jsonRequest = Result.fromContent(requestTable).toString();
		os.write(jsonRequest.getBytes("UTF-8"));
	}
	
	@Override
	protected void handleIOException(IOException err) {
	    super.handleIOException(err);
	}
}
