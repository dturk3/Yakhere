package com.dt.yakhere.app;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.codename1.io.NetworkManager;
import com.codename1.io.services.ImageDownloadService;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.Container;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.geom.Dimension;
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
			final String msg = feedItem.get("message");
			if (msg.startsWith("img:")) {
				try {
					final Image img = EncodedImage.create("/load.gif");
					final String imagePath = msg.substring(msg.indexOf('/') + 1, msg.length());
					final Container msgContainer = UiMessage.create(feedItem.get("publisher"), img, feedItem.get("fuzzyTimestamp"), (int)(0.67 * mChat.getWidth())).in(mChat);
					ImageDownloadService.createImageToStorage(Yakhere.BASE_URL + "/" + imagePath, (Label) msgContainer.getComponentAt(1), imagePath, new Dimension((int)(0.67 * mChat.getWidth()), (int)(0.67 * mChat.getWidth())));
				} 
				catch (IOException e) {
					continue;
				}
				continue;
			}
			UiMessage.create(feedItem.get("publisher"), msg, feedItem.get("fuzzyTimestamp")).in(mChat);
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
