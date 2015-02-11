package com.dt.yakhere.app;

import com.codename1.components.WebBrowser;
import com.codename1.io.NetworkManager;
import com.codename1.io.Oauth2;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.dt.yakhere.lib.GoogleIdRequest;
import com.dt.yakhere.lib.GoogleTokenRequest;

public class Authenticator {
	private final Form mMainForm;

	private static String mGoogleName = "";
	
	private static final String GOOGLE_CLIENT_ID = "189263345308-hscq73bajso48m8vd4a7lububjh5bhna.apps.googleusercontent.com";
	private static final String GOOGLE_CLIENT_SECRET = "W7Pzi6ULSVbMQdk2e-9436xy";
	
	public Authenticator(Form mainForm) {
		mMainForm = mainForm;
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
    	final Container subCenterContainer = new Container();
		subCenterContainer.setLayout(new BorderLayout());
		subCenterContainer.addComponent(BorderLayout.CENTER, authComponent);
		authComponent.setScrollable(true);
    	mMainForm.addComponent(0, subCenterContainer);
    	mMainForm.show();
    	
    	while (authComponent == null || authComponent.getTitle() == null || !authComponent.getTitle().startsWith("Success")) {
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
}
