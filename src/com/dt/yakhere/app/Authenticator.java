package com.dt.yakhere.app;

import com.codename1.components.WebBrowser;
import com.codename1.io.NetworkManager;
import com.codename1.io.Oauth2;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.painter.BackgroundPainter;
import com.codename1.ui.plaf.Style;
import com.dt.yakhere.Yakhere;
import com.dt.yakhere.lib.GoogleIdRequest;
import com.dt.yakhere.lib.GoogleTokenRequest;

public class Authenticator {
	private final Form mMainForm;
	private final Component mParentPanel;

	private static String mGoogleName = "";
	
	private static final String GOOGLE_CLIENT_ID = "189263345308-hscq73bajso48m8vd4a7lububjh5bhna.apps.googleusercontent.com";
	private static final String GOOGLE_CLIENT_SECRET = "W7Pzi6ULSVbMQdk2e-9436xy";
	
	public Authenticator(Form mainForm, Component signInPanel) {
		mMainForm = mainForm;
		mParentPanel = signInPanel;
	};
	
	public String authGoogle() throws InterruptedException {
    	Oauth2.setBackToParent(true);

    	final Oauth2 auth2 = new Oauth2(
			"https://accounts.google.com/o/oauth2/auth", 
			GOOGLE_CLIENT_ID, 
			"urn:ietf:wg:oauth:2.0:oob:auto", 
			"openid", 
			"https://accounts.google.com/o/oauth2/token", 
			GOOGLE_CLIENT_SECRET
    	);

    	final WebBrowser authComponent = (WebBrowser)auth2.createAuthComponent(new ActionListener() {
    	    	public void actionPerformed(ActionEvent evt) {
    	    		// No-op
    	    	}
    	    }
    	);
    	authComponent.setPreferredH(mMainForm.getPreferredH() - mMainForm.getTitleComponent().getPreferredH());
    	final Container subCenterContainer = new Container();
		subCenterContainer.setLayout(new BorderLayout());
		subCenterContainer.addComponent(BorderLayout.CENTER, authComponent);
		authComponent.setScrollable(true);
    	mMainForm.addComponent(0, subCenterContainer);
    	mMainForm.show();
    	
    	while (authComponent == null || authComponent.getTitle() == null || !authComponent.getTitle().startsWith("Success")) {
    		if (authComponent != null && authComponent.getTitle() != null && authComponent.getTitle().startsWith("Denied")) {
    			mMainForm.removeComponent(subCenterContainer);
    			authComponent.destroy();
    			throw new InterruptedException();
    		}
    		Thread.sleep(5000);
    	}
    	final String code = authComponent.getTitle().substring(authComponent.getTitle().lastIndexOf("=") + 1);
    	
    	final GoogleTokenRequest tokenRequest = new GoogleTokenRequest(code, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET);
    	NetworkManager.getInstance().addToQueueAndWait(tokenRequest);
    	
    	final String token = String.valueOf(tokenRequest.getResponse().get("access_token"));
    	
        final GoogleIdRequest request = new GoogleIdRequest(token);
		NetworkManager.getInstance().addToQueueAndWait(request);
		
		mMainForm.removeComponent(subCenterContainer);
		authComponent.destroy();
		
    	return String.valueOf(request.getResponse().get("given_name")) + String.valueOf(request.getResponse().get("family_name")).substring(0, 1);
	}
	
	public String signUp() {
    	Oauth2.setBackToParent(true);

    	final Container subCenterContainer = new Container();
		subCenterContainer.setLayout(new BorderLayout());
		final Style containerStyle = new Style();
		containerStyle.setBgColor(0);
		containerStyle.setBgTransparency(235);
		containerStyle.setBackgroundType(Style.BACKGROUND_GRADIENT_RADIAL, true);
		subCenterContainer.setPreferredH(mMainForm.getHeight());
		subCenterContainer.setSelectedStyle(containerStyle);

    	final Container topContainer = new Container();
    	topContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		final Style signInLabelStyle = new Style();
        Font signInInstructionsStyleFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        signInInstructionsStyleFont = signInInstructionsStyleFont.derive(36, Font.STYLE_PLAIN);
    	signInLabelStyle.setFont(signInInstructionsStyleFont);
    	signInLabelStyle.setFgColor(-1);
    	signInLabelStyle.setBgColor(0);
    	signInLabelStyle.setBgTransparency(235);
    	signInLabelStyle.setAlignment(Component.CENTER);
    	
    	final TextArea titleLabel = new TextArea("\nYak here, right now.\n");
    	titleLabel.setUnselectedStyle(signInLabelStyle);
    	titleLabel.setSelectedStyle(signInLabelStyle);
    	titleLabel.setEditable(false);
    	
    	topContainer.addComponent(titleLabel);
    	
    	final TextArea usernameLabel = new TextArea("username");
    	usernameLabel.setUnselectedStyle(signInLabelStyle);
    	usernameLabel.setSelectedStyle(signInLabelStyle);
    	usernameLabel.setEditable(false);
		
        Font fieldFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        fieldFont = fieldFont.derive(42, Font.STYLE_PLAIN);
        final TextField usernameField = new TextField();
        usernameField.getStyle().setFont(fieldFont);
        usernameField.setFocusable(false);
        usernameField.setPreferredW((int)(mMainForm.getWidth()) - 148);

        topContainer.addComponent(usernameLabel);
        topContainer.addComponent(usernameField);
        
    	final TextArea emailLabel = new TextArea("valid e-mail");
    	emailLabel.setUnselectedStyle(signInLabelStyle);
    	emailLabel.setSelectedStyle(signInLabelStyle);
    	emailLabel.setEditable(false);
        
        final TextField emailField = new TextField();
        emailField.getStyle().setFont(fieldFont);
        emailField.setFocusable(false);
        emailField.setPreferredW((int)(mMainForm.getWidth()) - 148);

        topContainer.addComponent(emailLabel);
        topContainer.addComponent(emailField);
        
    	final TextArea passwordLabel = new TextArea("password");
    	passwordLabel.setUnselectedStyle(signInLabelStyle);
    	passwordLabel.setSelectedStyle(signInLabelStyle);
    	passwordLabel.setEditable(false);
        
        final Component passwordField = new TextField();
        passwordField.getStyle().setFont(fieldFont);
        passwordField.setFocusable(false);
        passwordField.setPreferredW((int)(mMainForm.getWidth()) - 148);

        topContainer.addComponent(passwordLabel);
        topContainer.addComponent(passwordField);
        
    	final Container bottomContainer = new Container();
    	bottomContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

    	final Button back = new Button("back");
    	back.setPreferredH(64);
    	back.setPreferredW(mMainForm.getPreferredW()/2);
    	final Button signUp = new Button("sign up");
    	signUp.setPreferredH(64);
    	signUp.setPreferredW(mMainForm.getPreferredW()/2);

    	bottomContainer.addComponent(back);
        bottomContainer.addComponent(signUp);

		final Style disclaimerLabelStyle = new Style();
        Font disclaimerLabelStyleFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        disclaimerLabelStyleFont = signInInstructionsStyleFont.derive(14, Font.STYLE_PLAIN);
        disclaimerLabelStyle.setFont(disclaimerLabelStyleFont);
        disclaimerLabelStyle.setFgColor(-1);
        disclaimerLabelStyle.setBgColor(0);
        disclaimerLabelStyle.setBgTransparency(235);
        disclaimerLabelStyle.setAlignment(Component.CENTER);
    	
    	final TextArea disclaimerLabel = new TextArea("your email address will only be used for password retrieval purposes!");
    	disclaimerLabel.setUnselectedStyle(disclaimerLabelStyle);
    	disclaimerLabel.setSelectedStyle(disclaimerLabelStyle);
    	disclaimerLabel.setEditable(false);
        
    	bottomContainer.addComponent(disclaimerLabel);
    	
    	bottomContainer.setY(mMainForm.getHeight() - bottomContainer.getHeight());

    	subCenterContainer.addComponent(BorderLayout.NORTH, topContainer);
    	subCenterContainer.addComponent(BorderLayout.SOUTH, bottomContainer);
    	
    	mMainForm.addComponent(0, subCenterContainer);
    	mMainForm.show();
    	
    	back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				mParentPanel.setVisible(true);
				mMainForm.addComponent(0, mParentPanel);
				mMainForm.repaint();
			}
		});
    	
//		NetworkManager.getInstance().addToQueueAndWait(request);
		
//		mMainForm.removeComponent(subCenterContainer);
//		authComponent.destroy();
		
//    	return String.valueOf(request.getResponse().get("given_name")) + String.valueOf(request.getResponse().get("family_name")).substring(0, 1);
    	return "null";
	}
	
	public String signIn() {
    	Oauth2.setBackToParent(true);

    	final Container subCenterContainer = new Container();
		subCenterContainer.setLayout(new BorderLayout());
		final Style containerStyle = new Style();
		containerStyle.setBgColor(0);
		containerStyle.setBgTransparency(235);
		containerStyle.setBackgroundType(Style.BACKGROUND_GRADIENT_RADIAL, true);
		subCenterContainer.setPreferredH(mMainForm.getHeight());
		subCenterContainer.setSelectedStyle(containerStyle);

    	final Container topContainer = new Container();
    	topContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		
		final Style signInLabelStyle = new Style();
        Font signInInstructionsStyleFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        signInInstructionsStyleFont = signInInstructionsStyleFont.derive(36, Font.STYLE_PLAIN);
    	signInLabelStyle.setFont(signInInstructionsStyleFont);
    	signInLabelStyle.setFgColor(-1);
    	signInLabelStyle.setBgColor(0);
    	signInLabelStyle.setBgTransparency(235);
    	signInLabelStyle.setAlignment(Component.CENTER);
    	
    	final TextArea titleLabel = new TextArea("\nYak here, right now.\n");
    	titleLabel.setUnselectedStyle(signInLabelStyle);
    	titleLabel.setSelectedStyle(signInLabelStyle);
    	titleLabel.setEditable(false);
    	
    	topContainer.addComponent(titleLabel);
    	
    	final TextArea usernameLabel = new TextArea("username");
    	usernameLabel.setUnselectedStyle(signInLabelStyle);
    	usernameLabel.setSelectedStyle(signInLabelStyle);
    	usernameLabel.setEditable(false);
		
        Font fieldFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        fieldFont = fieldFont.derive(42, Font.STYLE_PLAIN);
        final TextField usernameField = new TextField();
        usernameField.getStyle().setFont(fieldFont);
        usernameField.setFocusable(false);
        usernameField.setPreferredW((int)(mMainForm.getWidth()) - 148);

        topContainer.addComponent(usernameLabel);
        topContainer.addComponent(usernameField);
        
    	final TextArea passwordLabel = new TextArea("password");
    	passwordLabel.setUnselectedStyle(signInLabelStyle);
    	passwordLabel.setSelectedStyle(signInLabelStyle);
    	passwordLabel.setEditable(false);
        
        final Component passwordField = new TextField();
        passwordField.getStyle().setFont(fieldFont);
        passwordField.setFocusable(false);
        passwordField.setPreferredW((int)(mMainForm.getWidth()) - 148);

        topContainer.addComponent(passwordLabel);
        topContainer.addComponent(passwordField);
        
    	final Container bottomContainer = new Container();
    	bottomContainer.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

    	final Button back = new Button("back");
    	back.setPreferredH(64);
    	back.setPreferredW(mMainForm.getPreferredW()/2);
    	final Button forgotPassword = new Button("forgot password");
    	forgotPassword.setPreferredH(64);
    	forgotPassword.setPreferredW(mMainForm.getPreferredW()/2);
    	final Button signUp = new Button("sign in");
    	signUp.setPreferredH(64);
    	signUp.setPreferredW(mMainForm.getPreferredW()/2);

    	bottomContainer.addComponent(back);
    	bottomContainer.addComponent(forgotPassword);
        bottomContainer.addComponent(signUp);

		final Style disclaimerLabelStyle = new Style();
        Font disclaimerLabelStyleFont = Font.createTrueTypeFont("Raleway Medium", "Raleway-Medium.ttf");
        disclaimerLabelStyleFont = signInInstructionsStyleFont.derive(14, Font.STYLE_PLAIN);
        disclaimerLabelStyle.setFont(disclaimerLabelStyleFont);
        disclaimerLabelStyle.setFgColor(-1);
        disclaimerLabelStyle.setBgColor(0);
        disclaimerLabelStyle.setBgTransparency(235);
        disclaimerLabelStyle.setAlignment(Component.CENTER);
    	
    	final TextArea disclaimerLabel = new TextArea("your email address will only be used for password retrieval purposes!");
    	disclaimerLabel.setUnselectedStyle(disclaimerLabelStyle);
    	disclaimerLabel.setSelectedStyle(disclaimerLabelStyle);
    	disclaimerLabel.setEditable(false);
        
    	bottomContainer.addComponent(disclaimerLabel);
    	
    	bottomContainer.setY(mMainForm.getHeight() - bottomContainer.getHeight());

    	subCenterContainer.addComponent(BorderLayout.NORTH, topContainer);
    	subCenterContainer.addComponent(BorderLayout.SOUTH, bottomContainer);
    	
    	mMainForm.addComponent(0, subCenterContainer);
    	mMainForm.show();
    	
    	back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				mParentPanel.setVisible(true);
				mMainForm.addComponent(0, mParentPanel);
				mMainForm.repaint();
			}
		});
    	
//		NetworkManager.getInstance().addToQueueAndWait(request);
		
//		mMainForm.removeComponent(subCenterContainer);
//		authComponent.destroy();
		
//    	return String.valueOf(request.getResponse().get("given_name")) + String.valueOf(request.getResponse().get("family_name")).substring(0, 1);
    	return "null";
	}
}
