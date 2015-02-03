package com.dt.yakhere;


import java.io.IOException;

import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.ContainerList;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableLayout.Constraint;
import com.codename1.ui.util.Resources;
import com.dt.yakhere.app.FeedRefreshThread;
import com.dt.yakhere.ui.UiMap;
import com.dt.yakhere.ui.UiMessage;

public class Yakhere {

    private Form current;
    
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
        
        /////////////////////////////////////////////////////////
        
        final ContainerList chat = new ContainerList();
        final BoxLayout chatLayout = new BoxLayout(BoxLayout.Y_AXIS);
        chat.setLayout(chatLayout);

        new FeedRefreshThread(chat).start(); 
        
        addMessage("fred", "Hello, world!", chat);
        addMessage("BOb", "yo buddy!", chat);
        addMessage("Andrew", "How does a really really really really really really long message look on this thing?!?!?!?!?!", chat);
        addMessage("BOb", "yo buddy!", chat);

        mainForm.addComponent(chat);
        
        mainForm.show();
    }

    @SuppressWarnings("deprecation")
	private Form initHomeScreen() throws IOException {
		final Form mainForm = new Form("YAKHERE");
        final BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
		mainForm.setLayout(layout);
		mainForm.setScrollable(false);
		
		//////////////////////////////////////////////
        
        final Container locationPanel = new Container();
        final TableLayout locationPanelLayout = new TableLayout(1, 2);
        final Constraint constraint = locationPanelLayout.createConstraint(0, 1);
        constraint.setWidthPercentage(100);
        locationPanelLayout.setGrowHorizontally(true);
		locationPanel.setLayout(locationPanelLayout);
		locationPanel.setScrollable(false);
        
        final Location location = LocationManager.getLocationManager().getCurrentLocation();
        UiMap.create(location).in(locationPanel);
        
        final Container locationTextPanel = new Container();
        locationTextPanel.setLayout(new TableLayout(2, 1));
        final Style panelStyle = new Style();
        panelStyle.setPadding(Component.TOP, 5);
        panelStyle.setPadding(Component.BOTTOM, 5);
        panelStyle.setPadding(Component.LEFT, 5);
        panelStyle.setPadding(Component.RIGHT, 5);
        panelStyle.setBgTransparency(98);
        panelStyle.setBgColor(5);
        locationTextPanel.setUnselectedStyle(panelStyle);
        locationTextPanel.setPreferredH(64);
        
        final Label name = new Label("fred");
        final Style nameStyle = new Style();
        Font nameFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        nameFont = nameFont.derive(18, Font.STYLE_PLAIN);
        nameStyle.setFont(nameFont);
        nameStyle.setFgColor(-1);
        nameStyle.setBgColor(0);
        nameStyle.setBgTransparency(0);
        name.setUnselectedStyle(nameStyle);
        locationTextPanel.addComponent(name);
        
        final Label hood = new Label("Grange Park");
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
        mainForm.addComponent(locationPanel);
        
        /////////////////////////////////////////////////////////
        
        final TextField input = new TextField();
        mainForm.addComponent(input);
        input.setPreferredW(240);
        
        final Image logo = Image.createImage("/yakhere.png").scaled(32, 32);
        final Button send = new Button();
        send.setIcon(logo);
        mainForm.addComponent(send);
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
}
