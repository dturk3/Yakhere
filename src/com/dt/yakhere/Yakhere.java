package com.dt.yakhere;


import java.io.IOException;

import com.codename1.capture.Capture;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.ContainerList;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableLayout.Constraint;
import com.codename1.ui.util.Resources;
import com.dt.yakhere.app.FeedRefreshThread;
import com.dt.yakhere.lib.GoogleGeocodingRequest;
import com.dt.yakhere.lib.Utils;
import com.dt.yakhere.lib.YakhereImageRequest;
import com.dt.yakhere.lib.YakhereMessageRequest;
import com.dt.yakhere.ui.UiMap;

public class Yakhere {
	public static final String BASE_URL = "http://localhost";	

    private Form current;
    private ContainerList chat; 
    private Button send;
    private TextField input;
    private Label name;
    private Label hood;
    private static Location location;
    
    public void init(Object context) {
        try{
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
       }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static Location getLocation() {
    	return location;
    }
    
    public static void updateLocation(Location newLocation) {
    	location = newLocation;
    }
    
	public void start() throws IOException {
        if(current != null){
            current.show();
            return;
        }
        final Form mainForm = initHomeScreen();
        send.addActionListener(new SendActionListener(input));
        
        /////////////////////////////////////////////////////////
        
        mainForm.show();
    }

    @SuppressWarnings("deprecation")
	private Form initHomeScreen() throws IOException {
		final Form mainForm = new Form("YakHere");
        Font titleFont = Font.createTrueTypeFont("Amerika", "AMERIKA_.ttf");
        titleFont = titleFont.derive(64, Font.STYLE_PLAIN);
        mainForm.getTitleComponent().getStyle().setFont(titleFont);
		
		final Container formPanel = new Container();
		final BorderLayout formPanelLayout = new BorderLayout();
		formPanel.setLayout(formPanelLayout);
		
		mainForm.addComponent(formPanel);
		
		final Container locationPanel = new Container();
        final TableLayout locationPanelLayout = new TableLayout(1, 2);
        final Constraint constraint = locationPanelLayout.createConstraint(0, 1);
        constraint.setWidthPercentage(100);
        locationPanelLayout.setGrowHorizontally(true);
		locationPanel.setLayout(locationPanelLayout);
		locationPanel.setScrollable(false);
        
        location = LocationManager.getLocationManager().getCurrentLocationSync();
        final Thread geocodeThread = new Thread() {
        	@Override
        	public void run() {
                final GoogleGeocodingRequest geocodingRequest = new GoogleGeocodingRequest(location.getLongitude(), 
                		location.getLatitude(), 
                		"AIzaSyAMBPwwIuZdYp1CcCL0OGKit0cFc70raXw");
                geocodingRequest.setContentType("application/json");
                geocodingRequest.setDuplicateSupported(true);
        		NetworkManager.getInstance().addToQueue(geocodingRequest);
        		while (!geocodingRequest.isReady()) {
        			// Wait...
        		}
        		hood.setText(geocodingRequest.getResponse());
        		hood.repaint();
        	}
        };
        geocodeThread.start();
        UiMap.create(location).in(locationPanel);
        
        final Container locationTextPanel = new Container();
        final TableLayout locationTextPanelLayout = new TableLayout(2, 1);
        locationTextPanelLayout.setGrowHorizontally(true);
		locationTextPanel.setLayout(locationTextPanelLayout);
        final Style panelStyle = new Style();
        panelStyle.setPadding(Component.TOP, 5);
        panelStyle.setPadding(Component.BOTTOM, 5);
        panelStyle.setPadding(Component.LEFT, 5);
        panelStyle.setPadding(Component.RIGHT, 5);
        panelStyle.setBgTransparency(98);
        panelStyle.setBgColor(5);
        locationTextPanel.setUnselectedStyle(panelStyle);
        locationTextPanel.setPreferredH(64);
        
        String yakhereName = String.valueOf(Storage.getInstance().readObject("yakhere-name"));
        if (yakhereName == null || "null".equals(yakhereName)) {
        	yakhereName = Utils.generateName();
        	Storage.getInstance().writeObject("yakhere-name", yakhereName);
        }
        
        name = new Label(yakhereName);
        
        final Style nameStyle = new Style();
        Font nameFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        nameFont = nameFont.derive(16, Font.STYLE_PLAIN);
        nameStyle.setFont(nameFont);
        nameStyle.setFgColor(-1);
        nameStyle.setBgColor(0);
        nameStyle.setBgTransparency(0);
        name.setUnselectedStyle(nameStyle);
        name.repaint();
        locationTextPanel.addComponent(name);
        
        hood = new Label("- - -");
        final Style hoodStyle = new Style();
        Font hoodFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        hoodFont = hoodFont.derive(14, Font.STYLE_PLAIN);
        hoodStyle.setFont(nameFont);
        hoodStyle.setFgColor(-1);
        hoodStyle.setBgColor(0);
        hoodStyle.setBgTransparency(0);
        hood.setUnselectedStyle(hoodStyle);
        locationTextPanel.addComponent(hood);
        
        locationPanel.addComponent(locationTextPanel);
        formPanel.addComponent(BorderLayout.NORTH, locationPanel);
        
		chat = new ContainerList();
		final BoxLayout chatLayout = new BoxLayout(BoxLayout.Y_AXIS);
        chat.setLayout(chatLayout);
        chat.setScrollableY(true);
        chat.setFocusable(false);
        new FeedRefreshThread(chat).start(); 
        formPanel.addComponent(BorderLayout.CENTER, chat);

        final BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
		mainForm.setLayout(layout);
		mainForm.setScrollable(false);
		
        /////////////////////////////////////////////////////////
        
		final Container controlsPanel = new Container(new BoxLayout(BoxLayout.Y_AXIS));		
        input = new TextField();
        input.setFocusable(false);
        input.setPreferredW((int)(0.8 * mainForm.getWidth()));
        
        final Button cameraButton = new Button();
        cameraButton.addActionListener(new CameraActionListener());
        cameraButton.setPreferredW((int)(0.2 * mainForm.getWidth()));
        final Image camera = Image.createImage("/camera-light.png");
        cameraButton.setIcon(camera);
        final Container inputContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        inputContainer.addComponent(input);
        inputContainer.addComponent(cameraButton);
        controlsPanel.addComponent(inputContainer);
        
        final Image logo = Image.createImage("/yakhere.png").scaled(32, 32);
        send = new Button();
        send.setIcon(logo);
        send.setFocusable(false);
        controlsPanel.addComponent(send);
        
		chat.setPreferredH(mainForm.getHeight()-(35 + 
				send.getPreferredH() + 2*send.getStyle().getMargin(Component.TOP) + 
				input.getPreferredH() + 2*input.getStyle().getMargin(Component.TOP) + 
				locationTextPanel.getPreferredH() + 2*locationTextPanel.getStyle().getMargin(Component.TOP) + 
				mainForm.getTitleComponent().getPreferredH() + 2*mainForm.getTitleComponent().getStyle().getMargin(Component.BOTTOM)));
		formPanel.addComponent(BorderLayout.SOUTH, controlsPanel);
		return mainForm;
	}

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
    
    private class CameraActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			final String capturedPhoto = Capture.capturePhoto(306, 306);
			
			try {
				final MultipartRequest imageUploadRequest = new YakhereImageRequest(location.getLatitude(), location.getLongitude(), capturedPhoto, name.getText());
	            imageUploadRequest.setUrl(BASE_URL + "/upload");
	            imageUploadRequest.setPost(true);
	            imageUploadRequest.setDuplicateSupported(true);
	            try {
	                imageUploadRequest.addData("content", capturedPhoto, "image/png");
	                imageUploadRequest.setFilename("content", capturedPhoto);
	                NetworkManager.getInstance().addToQueue(imageUploadRequest);
	            } 
	            catch (IOException ioe) {
	                return;
	            }
			}
			catch (Exception e) {
				return;
			}
		}
	}
    
    private class SendActionListener implements ActionListener {
    	final TextField mInput;
    	
    	public SendActionListener(TextField inputFrom) {
    		mInput = inputFrom;
		}
    	
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (mInput.getText().length() < 1) {
				return;
			}
			if (mInput.getText().length() > 250) {
				return;
			}
			Location location = LocationManager.getLocationManager().getCurrentLocationSync();
			// TODO - longitude and latitude are mixed up
			final YakhereMessageRequest msgRequest = new YakhereMessageRequest(location.getLatitude(), location.getLongitude(), input.getText(), name.getText());
			msgRequest.setUrl(BASE_URL + "/messages");
			msgRequest.setPost(true);
			msgRequest.setContentType("application/json");
			msgRequest.setDuplicateSupported(true);
			NetworkManager.getInstance().addToQueue(msgRequest);
			mInput.setHeight(mInput.getPreferredH());
			((Component)evt.getSource()).setHeight(((Component)evt.getSource()).getPreferredH());
			mInput.setText("");
		}
	}
}
