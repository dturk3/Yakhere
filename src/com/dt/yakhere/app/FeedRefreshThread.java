package com.dt.yakhere.app;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.codename1.io.NetworkManager;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.dt.yakhere.Yakhere;
import com.dt.yakhere.lib.YakhereFeedRequest;
import com.dt.yakhere.ui.UiMessage;

public class FeedRefreshThread extends Thread {
	private final Container mChat;
	
	public FeedRefreshThread(Container toRefresh) {
		mChat = toRefresh;
	}
	
	@Override
	public void run() {
    	try {
			Yakhere.updateLocation(refreshLocation());
	        while (true) {
	        	final long now = System.currentTimeMillis() / 1000;
        		if (now % 2 == 0) {
        			refreshFeed(Yakhere.getLocation());
        		}
        		if (now % 10 == 0) {
        			Yakhere.updateLocation(refreshLocation());
        		}
                Thread.sleep(1000);
	        }
		} 
    	catch (InterruptedException | IOException e) {
				throw new IllegalStateException("Refresh thread failed!");
		}
    }

	private void refreshFeed(Location location) {
		// TODO - Longitude and latitude are mixed up here!
		final YakhereFeedRequest yakhereRequest = new YakhereFeedRequest(location.getLatitude(), location.getLongitude());
		yakhereRequest.setUrl(Yakhere.BASE_URL + "/feeds");
		yakhereRequest.setHttpMethod("POST");
		yakhereRequest.setPost(true);
		yakhereRequest.setContentType("application/json");
		yakhereRequest.setDuplicateSupported(true);
		NetworkManager.getInstance().addToQueue(yakhereRequest);
		
		while(!yakhereRequest.isReady()) {
			// Wait...
		}
			
		int newItems = yakhereRequest.getResponse().size() - mChat.getComponentCount();
		List<Map<String, String>> feedList = yakhereRequest.getResponse();
		feedList = feedList.subList(feedList.size() - newItems, feedList.size());
		for (Map<String, String> feedItem: feedList) {
			UiMessage.create(feedItem.get("publisher"), feedItem.get("message"), feedItem.get("fuzzyTimestamp")).in(mChat);
		}
		mChat.repaint();
	}

	private Location refreshLocation() throws IOException {
		final Location oldLocation = Yakhere.getLocation();
		final Location newLocation = LocationManager.getLocationManager().getCurrentLocationSync();
		if (!newLocation.equals(oldLocation)) {
			mChat.removeAll();
		}
		return newLocation;
	}
}
