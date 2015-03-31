package com.dt.yakhere.app;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.NetworkManager;
import com.codename1.io.services.ImageDownloadService;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.dt.yakhere.Yakhere;
import com.dt.yakhere.lib.Utils;
import com.dt.yakhere.lib.YakhereFeedRequest;
import com.dt.yakhere.ui.UiMessage;

public class FeedRefreshThread extends Thread {
	private final Container mChat;
	
	public FeedRefreshThread(Container toRefresh) {
		mChat = toRefresh;
	}
	
	@Override
	public void run() {
		final Container spinnerContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		if (mChat.getComponentCount() < 1) {
			final Style spinnerContainerStyle = new Style();
			spinnerContainerStyle.setAlignment(Component.CENTER);
			spinnerContainerStyle.setMargin(Component.TOP, 100);
			spinnerContainer.setUnselectedStyle(spinnerContainerStyle);
			spinnerContainer.setFocusable(false);
			
        	final Style spinnerStyle = new Style();
            Font fckErrorStyleFont = Font.createTrueTypeFont("FrenteH1-Regular", "FrenteH1-Regular.ttf");
            fckErrorStyleFont = fckErrorStyleFont.derive(42, Font.STYLE_PLAIN);
            spinnerStyle.setFont(fckErrorStyleFont);
            spinnerStyle.setFgColor(0);
            spinnerStyle.setBgTransparency(235);
            spinnerStyle.setAlignment(Component.CENTER);
        	
        	final InfiniteProgress spinner = new InfiniteProgress();
        	spinner.setUnselectedStyle(spinnerStyle);
        	
        	spinnerContainer.addComponent(spinner);
        	spinnerStyle.setMargin(Component.LEFT, mChat.getParent().getPreferredW()/2 - (int)(spinner.getPreferredW()*1.5));
        	
			mChat.addComponent(0, spinnerContainer);
			mChat.repaint();
		}
		
    	try {
			Yakhere.updateLocation(refreshLocation());
	        while (true) {
	        	final long now = System.currentTimeMillis() / 1000;
        		if (now % 2 == 0) {
        			refreshFeed(Yakhere.getLocation(), spinnerContainer);
        		}
        		if (now % 10 == 0) {
        			Yakhere.updateLocation(refreshLocation());
        		}
                Thread.sleep(1000);
	        }
		} 
    	catch (Exception e) {
    		mChat.removeAll();
    		
			final Container logoContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
			final Style logoContainerStyle = new Style();
			logoContainerStyle.setAlignment(Component.CENTER);
			logoContainerStyle.setMargin(Component.TOP, 100);
			logoContainer.setUnselectedStyle(logoContainerStyle);
			logoContainer.setFocusable(false);
			
        	final Style fckErrorStyle = new Style();
            Font fckErrorStyleFont = Font.createTrueTypeFont("FrenteH1-Regular", "FrenteH1-Regular.ttf");
            fckErrorStyleFont = fckErrorStyleFont.derive(42, Font.STYLE_PLAIN);
            fckErrorStyle.setFont(fckErrorStyleFont);
            fckErrorStyle.setFgColor(0);
            fckErrorStyle.setBgTransparency(235);
            fckErrorStyle.setAlignment(Component.CENTER);
        	
        	final TextArea fckError = new TextArea("F*CK, ERROR");
        	fckError.setUnselectedStyle(fckErrorStyle);
        	fckError.setSelectedStyle(fckErrorStyle);
        	fckError.setEditable(false);
        	fckError.setDraggable(false);

			logoContainer.addComponent(fckError);
        	
			mChat.addComponent(0, logoContainer);
			mChat.repaint();

			throw new IllegalStateException("Refresh thread failed!");
		}
    }

	private void refreshFeed(Location location, Component spinner) throws IOException {
		// TODO - Longitude and latitude are mixed up here!
		final YakhereFeedRequest yakhereRequest = new YakhereFeedRequest(location.getLatitude(), location.getLongitude());
		yakhereRequest.setUrl(Yakhere.BASE_URL + "/feeds");
		yakhereRequest.setHttpMethod("POST");
		yakhereRequest.setPost(true);
		yakhereRequest.setContentType("application/json");
		yakhereRequest.setDuplicateSupported(true);
		
		NetworkManager.getInstance().addToQueueAndWait(yakhereRequest);
        mChat.removeComponent(spinner);

		int newItems = yakhereRequest.getResponse().size() - mChat.getComponentCount();
		newItems = Math.max(newItems, 0);
		List<Map<String, String>> feedList = yakhereRequest.getResponse();
		final List<Map<String, String>> newFeedList = feedList.subList(feedList.size() - newItems, feedList.size());
		
		for (Map<String, String> feedItem: newFeedList) {
			
			final String msg = feedItem.get("message");
			if (msg.startsWith("img:")) {
				try {
					final Image img = Utils.loadImage("/load.gif");
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
		
		for (int i = 0; i < feedList.size(); i++) {
			final Component comp = mChat.getComponentAt(i);
			if (comp instanceof Container) {
				final Container containerComp = (Container)comp;
				final Label timestamp = (Label) ((Container)containerComp.getComponentAt(0)).getComponentAt(1);
				timestamp.setText(feedList.get(feedList.size() - 1 - i).get("fuzzyTimestamp"));
			}
		}
		
		if (mChat.getComponentCount() < 1) {
			final Image logo = Utils.loadImage("/yakhere-dark.png");
			final Container logoContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
			final Label logoLabel = new Label(logo.scaled(64, 64));
			final Style logoContainerStyle = new Style();
			logoContainerStyle.setAlignment(Component.CENTER);
			logoContainerStyle.setMargin(Component.TOP, 50);
			logoLabel.setUnselectedStyle(logoContainerStyle);
			logoContainer.setUnselectedStyle(logoContainerStyle);
			logoContainer.setFocusable(false);
			
			logoContainer.addComponent(logoLabel);
			
        	final Style quietHereStyle = new Style();
            Font quietHereStyleFont = Font.createTrueTypeFont("FrenteH1-Regular", "FrenteH1-Regular.ttf");
            quietHereStyleFont = quietHereStyleFont.derive(42, Font.STYLE_PLAIN);
            quietHereStyle.setFont(quietHereStyleFont);
            quietHereStyle.setFgColor(0);
            quietHereStyle.setBgTransparency(235);
            quietHereStyle.setAlignment(Component.CENTER);
        	
        	final TextArea quietHere = new TextArea("QUIET\nHERE");
        	quietHere.setUnselectedStyle(quietHereStyle);
        	quietHere.setSelectedStyle(quietHereStyle);
        	quietHere.setEditable(false);
        	quietHere.setPreferredH(110);
        	quietHere.setDraggable(false);

			logoContainer.addComponent(quietHere);
        	
			mChat.addComponent(0, logoContainer);
			mChat.repaint();
		}
		else {
			final Component quietLabel = mChat.getComponentAt(0);
			if (quietLabel instanceof Label) {
				mChat.removeComponent(quietLabel);
			}
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
