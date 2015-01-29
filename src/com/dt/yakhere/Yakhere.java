package com.dt.yakhere;


import java.io.IOException;

import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.ContainerList;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableLayout.Constraint;
import com.codename1.ui.util.Resources;

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
        
        final GoogleMapsProvider mapProvider = new GoogleMapsProvider("AIzaSyAMBPwwIuZdYp1CcCL0OGKit0cFc70raXw");
        mapProvider.translate(new Coord(location.getLatitude(), location.getLongitude()), 15, 75, 75);
        final MapComponent envMap = new MapComponent(mapProvider);
        
        final Style mapStyle = new Style();
        final Border mapBorder = Border.createBevelRaised();
        mapBorder.setThickness(1);
        mapStyle.setBorder(mapBorder);
        mapStyle.setMargin(Component.LEFT, 7);
        mapStyle.setMargin(Component.RIGHT, 5);
        envMap.setUnselectedStyle(mapStyle);
        
        envMap.setDraggable(false);
        envMap.setZoomLevel(9);
        envMap.setPreferredSize(new Dimension(64, 64));
        envMap.removeAll();
        
        locationPanel.addComponent(envMap);
        
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

    @SuppressWarnings("deprecation")
	private void addMessage(String publisherName, String msgText, ContainerList chat) {
		final Container msg = new Container();
        final Border msgBorder = Border.createEtchedRaised();
        final TableLayout msgLayout = new TableLayout(1, 2);
        final Constraint rightConstraint = msgLayout.createConstraint(0, 1);
        rightConstraint.setWidthPercentage(100);
        msgLayout.setGrowHorizontally(true);
        msg.setLayout(msgLayout);
        final Style msgStyle = new Style();
        msgStyle.setBorder(msgBorder);
        msgStyle.setPadding(Component.TOP, 5);
        msgStyle.setPadding(Component.RIGHT, 5);
        msgStyle.setPadding(Component.BOTTOM, 5);
        msgStyle.setPadding(Component.LEFT, 5);
        msgStyle.setMargin(Component.BOTTOM, 1);
        msgStyle.setBgColor(-1);
        msg.setUnselectedStyle(msgStyle);
        
        Font textFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        textFont = textFont.derive(18, Font.STYLE_PLAIN);
        
        final Label publisher = new Label(publisherName);
        final Style publisherStyle = new Style();
        Font publisherFont = Font.createTrueTypeFont("Alexandria", "AlexandriaFLF.ttf");
        publisherFont = publisherFont.derive(14, Font.STYLE_PLAIN);
        publisherStyle.setFont(textFont);
        publisherStyle.setBgColor(0);
        publisherStyle.setBgTransparency(0);
        publisher.setUnselectedStyle(publisherStyle);
        publisher.setPreferredW(50);
        
        final TextArea item = new TextArea(msgText);
        item.setEditable(false);
        item.setFocusable(false);
        final Style itemStyle = new Style();
        Font itemFont = Font.createTrueTypeFont("Alexandria", "AlexandriaFLF.ttf");
        itemFont = itemFont.derive(18, Font.STYLE_PLAIN);
        itemStyle.setFont(textFont);
        itemStyle.setBgColor(0);
        itemStyle.setBgTransparency(0);
        item.setGrowByContent(true);
        item.setUnselectedStyle(itemStyle);
        
        msg.addComponent(publisher);
        msg.addComponent(item);
        
        chat.addComponent(msg);
	}

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
}
