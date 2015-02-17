package com.dt.yakhere.ui;

import com.codename1.location.Location;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.maps.providers.MapProvider;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;

public class UiMap extends MapComponent{
    private static final String GMAPS_API_KEY = "AIzaSyAMBPwwIuZdYp1CcCL0OGKit0cFc70raXw";
	
	public UiMap(MapProvider provider) {
		super(provider);
	}
	
	@SuppressWarnings("deprecation")
	public static UiMap create(Location location) {
		final GoogleMapsProvider mapProvider = new GoogleMapsProvider(GMAPS_API_KEY);
        final Coord position = new Coord(location.getLatitude(), location.getLongitude());
		mapProvider.translate(position, 15, 75, 75);
		
        final UiMap envMap = new UiMap(mapProvider);
        
        final Style mapStyle = new Style();
        final Border mapBorder = Border.createBevelRaised();
        mapBorder.setThickness(1);
        mapStyle.setBorder(mapBorder);
        mapStyle.setMargin(Component.LEFT, 7);
        mapStyle.setMargin(Component.RIGHT, 5);
        envMap.setUnselectedStyle(mapStyle);
        
        envMap.setDraggable(false);
        envMap.setZoomLevel(15);
        envMap.setPreferredSize(new Dimension(96, 96));
        envMap.removeAll();
        envMap.repaint();
        
        return envMap;
	}
	
	public Component in(Container container) {
		container.addComponent(this);
		return this;
	}
}
