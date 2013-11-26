package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


public class FeedbackActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.v2_feedback_layout);
	
		 PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			((TextView)findViewById(R.id.version)).setText(getString(R.string.version) 
					+ pInfo.versionName);
		} catch (NameNotFoundException e) {
			logException(e.getMessage(), false);
		}
		 
		
	
	}
	
	
	public void goToDrilAppPage(View view){
		EasyTracker tracker = EasyTracker.getInstance(this);
		tracker.send(MapBuilder.createEvent(
					"ui_action", 
					"button_press", 
					"dril_homepage", 
					null)
				.build());
		goToUrl ( Constants.DRIL_HOMEPAGE_URL );
	}
	
	
	public void goWebPage(View view){
		goToUrl ( Constants.PORTFOLIO_URL );
	}
	
	
	
	public void goToFbPage(View view){
		goToUrl ( Constants.FACEBOOK_URL );
	}
	
	public void donateDril(View view) {
		EasyTracker tracker = EasyTracker.getInstance(this);
		tracker.send(MapBuilder.createEvent(
					"ui_action", 
					"button_press", 
					"donate", 
					null)
				.build());
		goToUrl ( Constants.DONATE_URL );
	}
	
	
	 private void goToUrl (String url) {
	        Uri uriUrl = Uri.parse(url);
	        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	        startActivity(launchBrowser);
	    }
	
	 
	 
	public void sendEmail(View view){
		 /* Create the Intent */
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		/* Fill it with Data */
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Constants.CONTACT_EMAIL});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Dril");

		startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)) );
	}
	
	
}
