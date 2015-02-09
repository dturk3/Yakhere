package com.dt.yakhere.ui;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableLayout.Constraint;

public class UiMessage extends Container {
	public static UiMessage create(String publisherName, Image image, String fuzzyTime, int imageSize) {
		final Label item = buildImageMessage(image, imageSize);
		return buildMessage(publisherName, item, fuzzyTime);
	}
	
	public static UiMessage create(String publisherName, String msgText, String fuzzyTime) {
        final TextArea item = buildTextMsg(msgText);
		return buildMessage(publisherName, item, fuzzyTime);
	}

	private static UiMessage buildMessage(String publisherName, Component msgText,
			String fuzzyTime) {
		final UiMessage msg = new UiMessage();
        final TableLayout msgLayout = new TableLayout(3, 2);
        final Constraint rightConstraint = msgLayout.createConstraint(0, 1);
        rightConstraint.setWidthPercentage(100);
        rightConstraint.setVerticalSpan(3);
        msgLayout.setGrowHorizontally(true);
        msg.setLayout(msgLayout);
        msg.setUnselectedStyle(msgStyle());
        
        final Label publisher = new Label(publisherName);
        final Style publisherStyle = new Style();
        Font publisherFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        publisherFont = publisherFont.derive(16, Font.STYLE_PLAIN);
        publisherStyle.setBgColor(0);
        publisherStyle.setBgTransparency(0);
        publisherStyle.setFont(publisherFont);
        publisher.setUnselectedStyle(publisherStyle);
        
        final Label timestamp = new Label(fuzzyTime);
        final Style timestampStyle = new Style();
        Font timestampFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        timestampFont = timestampFont.derive(10, Font.STYLE_PLAIN);
        timestampStyle.setFont(timestampFont);
        timestamp.setUnselectedStyle(timestampStyle);

        msg.addComponent(publisher);
        msg.addComponent(msgText);
        msg.addComponent(timestamp);
        
        final Label spacer = new Label();
        Font spacerFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        spacerFont = spacerFont.derive(16, Font.STYLE_PLAIN);
        final Style spacerStyle = new Style();
        spacerStyle.setFont(spacerFont);
        spacerStyle.setFgColor(-1);
        spacer.setUnselectedStyle(spacerStyle);
        spacer.setText("AAAAAAAAAA");
        msg.addComponent(new Label());
        msg.addComponent(spacer);
        
        return msg;
	}

	private static TextArea buildTextMsg(String msgText) {
		final TextArea item = new TextArea(msgText);
        item.setEditable(false);
        item.setFocusable(false);
        final Style itemStyle = new Style();
        Font itemFont = Font.createTrueTypeFont("Folks", "Folks-Normal.ttf");
        itemFont = itemFont.derive(18, Font.STYLE_PLAIN);
        itemStyle.setFont(itemFont);
        itemStyle.setBgColor(0);
        itemStyle.setBgTransparency(0);
        item.setGrowByContent(true);
        item.setUnselectedStyle(itemStyle);
		return item;
	}
	
	private static Label buildImageMessage(Image image, int imageSize) {
		final Label item = new Label(image.scaled(imageSize, imageSize));
        item.setFocusable(false);
        final Style itemStyle = new Style();
        itemStyle.setBgColor(0);
        itemStyle.setBgTransparency(0);
        itemStyle.setBorder(Border.createGrooveBorder(1));
        item.setUnselectedStyle(itemStyle);
		return item;
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
		container.addComponent(0, this);
		int newHeight = 200;
		for (int i = 0; i < container.getComponentCount(); i++) {
			newHeight += container.getComponentAt(i).getPreferredH();
		}
		container.setPreferredH(newHeight);
		return this;
	}
}
