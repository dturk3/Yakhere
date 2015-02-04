package com.dt.yakhere;


import java.io.IOException;

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
import com.dt.yakhere.lib.YakhereMessageRequest;
import com.dt.yakhere.ui.UiMap;
import com.dt.yakhere.ui.UiMessage;

public class Yakhere {

    private Form current;
    private ContainerList chat; 
    private Button send;
    private TextField input;
    private Label name;
    private Label hood;
    
    public void init(Object context) {
        try{
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
       }catch(IOException e){
            e.printStackTrace();
        }
    }
    
	public void start() throws IOException {
        if(current != null){
            current.show();
            return;
        }
        final Form mainForm = initHomeScreen();
        send.addActionListener(new SendActionListener(input, chat));
        
        /////////////////////////////////////////////////////////
        
        addMessage("BOb", "yo buddy!", chat);
        
        mainForm.show();
    }

    @SuppressWarnings("deprecation")
	private Form initHomeScreen() throws IOException {
		final Form mainForm = new Form("YAKHERE");
        Font titleFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        titleFont = titleFont.derive(36, Font.STYLE_PLAIN);
        mainForm.getTitleComponent().getStyle().setFont(titleFont);
		
		final Container formPanel = new Container();
		final TableLayout formPanelLayout = new TableLayout(4, 1);
		final Constraint topRow = formPanelLayout.createConstraint(0, 0);
		topRow.setHeightPercentage(10);
		final Constraint midRow = formPanelLayout.createConstraint(1, 0);
		midRow.setHeightPercentage(70);
		final Constraint botRow = formPanelLayout.createConstraint(2, 0);
		botRow.setHeightPercentage(10);
		final Constraint sendRow = formPanelLayout.createConstraint(3, 0);
		sendRow.setHeightPercentage(10);
		formPanel.setLayout(formPanelLayout);
		
		mainForm.addComponent(formPanel);
		
		final Container locationPanel = new Container();
        final TableLayout locationPanelLayout = new TableLayout(1, 2);
        final Constraint constraint = locationPanelLayout.createConstraint(0, 1);
        constraint.setWidthPercentage(100);
        locationPanelLayout.setGrowHorizontally(true);
		locationPanel.setLayout(locationPanelLayout);
		locationPanel.setScrollable(false);
        
        final Location location = LocationManager.getLocationManager().getCurrentLocationSync();
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
        formPanel.addComponent(locationPanel);
        
		chat = new ContainerList();
		final BoxLayout chatLayout = new BoxLayout(BoxLayout.Y_AXIS);
        chat.setLayout(chatLayout);
        new FeedRefreshThread(chat).start(); 
        formPanel.addComponent(chat);

        final BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
		mainForm.setLayout(layout);
		mainForm.setScrollable(false);
		
        /////////////////////////////////////////////////////////
        
        input = new TextField();
        formPanel.addComponent(input);
        input.setPreferredW(240);
        
        final Image logo = Image.createImage("/yakhere.png").scaled(32, 32);
        send = new Button();
        send.setIcon(logo);
        formPanel.addComponent(send);
        
		chat.setPreferredH(mainForm.getHeight()-(35 + 
				send.getPreferredH() + 2*send.getStyle().getMargin(Component.TOP) + 
				input.getPreferredH() + 2*input.getStyle().getMargin(Component.TOP) + 
				locationTextPanel.getPreferredH() + 2*locationTextPanel.getStyle().getMargin(Component.TOP) + 
				mainForm.getTitleComponent().getPreferredH() + 2*mainForm.getTitleComponent().getStyle().getMargin(Component.BOTTOM)));
        
		return mainForm;
	}

	private void addMessage(String publisherName, String msgText, ContainerList chat) {
		UiMessage.create(publisherName, msgText).in(chat);
	}

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
    
    private class SendActionListener implements ActionListener {
    	final TextField mInput;
    	final ContainerList mOutput;
    	
    	public SendActionListener(TextField inputFrom, ContainerList outputTo) {
    		mInput = inputFrom;
    		mOutput = outputTo;
		}
    	
		@Override
		public void actionPerformed(ActionEvent evt) {
			Location location = LocationManager.getLocationManager().getCurrentLocationSync();
			// TODO - longitude and latitude are mixed up
			final YakhereMessageRequest msgRequest = new YakhereMessageRequest(location.getLatitude(), location.getLongitude(), input.getText(), name.getText());
			msgRequest.setUrl("http://www.yakhere.com/messages");
			msgRequest.setPost(true);
			msgRequest.setContentType("application/json");
			msgRequest.setDuplicateSupported(true);
			NetworkManager.getInstance().addToQueue(msgRequest);
			UiMessage.create(name.getText(), mInput.getText()).in(mOutput);
			mOutput.repaint();
		}
	}
}
