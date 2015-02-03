package com.dt.yakhere.app;

import java.io.IOException;
import java.util.Map;

import com.codename1.io.NetworkManager;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.Container;
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
			Location location = refreshLocation();
	        while (true) {
	        	final long now = System.currentTimeMillis() / 1000;
        		if (now % 2 == 0) {
        			refreshFeed(location);
        		}
        		if (now % 10 == 0) {
        			location = refreshLocation();
        		}
                Thread.sleep(1000);
	        }
		} 
    	catch (InterruptedException | IOException e) {
				throw new IllegalStateException("Refresh thread failed!");
		}
    }

	private void refreshFeed(Location location) {
		final YakhereFeedRequest yakhereRequest = new YakhereFeedRequest(location.getLongitude(), location.getLatitude());
		yakhereRequest.setUrl("http://www.yakhere.com/feeds");
		yakhereRequest.setPost(true);
		yakhereRequest.setContentType("application/json");
		yakhereRequest.setDuplicateSupported(true);
		NetworkManager.getInstance().addToQueue(yakhereRequest);
		
		while(!yakhereRequest.isReady()) {
			// Wait...
		}
		
		for (Map<String, String> feedItem: yakhereRequest.getResponse()) {
			UiMessage.create(feedItem.get("publisher"), feedItem.get("message")).in(mChat);
		}
	}

	private static Location refreshLocation() throws IOException {
		return LocationManager.getLocationManager().getCurrentLocation();
	}
}
