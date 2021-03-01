package sk.peterjurkovic.dril.v2.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;


public class FeedbackActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.v2_feedback_layout);
	
		 PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			((TextView)findViewById(R.id.version)).setText( getString(R.string.version)  	+" "+ pInfo.versionName);
		} catch (NameNotFoundException e) {
			GoogleAnalyticsUtils.logException(e);
			Log.e(getClass().getSimpleName(), e.getMessage());
		}
		 
		
	
	}
	
	
	public void goToDrilAppPage(View view){
		goToUrl ( Constants.DRIL_HOMEPAGE_URL );
	}
	
	
	public void goWebPage(View view){
		goToUrl ( Constants.PORTFOLIO_URL );
	}
	
	
	
	public void goToFbPage(View view){
		goToUrl ( Constants.FACEBOOK_URL );
	}
	
	public void donateDril(View view) {
		goToUrl ( Constants.DONATE_URL );
	}
	
	
	 private void goToUrl (String url) {
	        Uri uriUrl = Uri.parse(url);
	        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	        PackageManager pm = getPackageManager();
	        ResolveInfo resolveInfo = pm.resolveActivity( launchBrowser, PackageManager.MATCH_DEFAULT_ONLY );
	        if( resolveInfo != null ) {
	        	 startActivity(launchBrowser);
	        }
	        
	    }
	
	 
	 
	public void sendEmail(View view){
		 /* Create the Intent */
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);

		/* Fill it with Data */
		emailIntent.setType("plain/text");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.CONTACT_EMAIL});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Dril");

		startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)) );
	}
	
	
}
