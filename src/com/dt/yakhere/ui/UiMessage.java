package com.dt.yakhere.ui;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableLayout.Constraint;

public class UiMessage extends Container {
	public static UiMessage create(String publisherName, String msgText) {
		final UiMessage msg = new UiMessage();
        final TableLayout msgLayout = new TableLayout(1, 2);
        final Constraint rightConstraint = msgLayout.createConstraint(0, 1);
        rightConstraint.setWidthPercentage(100);
        msgLayout.setGrowHorizontally(true);
        msg.setLayout(msgLayout);
        msg.setUnselectedStyle(msgStyle());
        
        Font textFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        textFont = textFont.derive(16, Font.STYLE_PLAIN);
        
        final Label publisher = new Label(publisherName);
        final Style publisherStyle = new Style();
        Font publisherFont = Font.createTrueTypeFont("Alexandria", "AlexandriaFLF.ttf");
        publisherFont = publisherFont.derive(16, Font.STYLE_PLAIN);
        publisherStyle.setFont(textFont);
        publisherStyle.setBgColor(0);
        publisherStyle.setBgTransparency(0);
        publisherStyle.setFont(publisherFont);
        publisher.setUnselectedStyle(publisherStyle);
        publisher.setPreferredW(50);
        
        final TextArea item = new TextArea(msgText);
        item.setEditable(false);
        item.setFocusable(false);
        final Style itemStyle = new Style();
        Font itemFont = Font.createTrueTypeFont("Alexandria", "AlexandriaFLF.ttf");
        itemFont = itemFont.derive(18, Font.STYLE_PLAIN);
        itemStyle.setFont(itemFont);
        itemStyle.setBgColor(0);
        itemStyle.setBgTransparency(0);
        item.setGrowByContent(true);
        item.setUnselectedStyle(itemStyle);
        
        msg.addComponent(publisher);
        msg.addComponent(item);
        
        return msg;
	}

	private static Style msgStyle() {
		final Style msgStyle = new Style();
        msgStyle.setBorder(Border.createEtchedRaised());
        msgStyle.setPadding(Component.TOP, 5);
        msgStyle.setPadding(Component.RIGHT, 5);
        msgStyle.setPadding(Component.BOTTOM, 5);
        msgStyle.setPadding(Component.LEFT, 5);
        msgStyle.setMargin(Component.BOTTOM, 1);
        msgStyle.setBgColor(-1);
		return msgStyle;
	}
	
	public Container in(Container container) {
		container.addComponent(this);
		int newHeight = 200;
		for (int i = 0; i < container.getComponentCount(); i++) {
			newHeight += container.getComponentAt(i).getPreferredH();
		}
		container.setPreferredH(Math.max(container.getPreferredH(), newHeight));
		container.repaint();
		return this;
	}
}
