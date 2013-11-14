package sk.peterjurkovic.dril;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.main_about);
	
		 PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			((TextView)findViewById(R.id.version)).setText(getString(R.string.version) 
					+ pInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		 
		
		ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
               // startActivity( new Intent(AboutActivity.this, DashboardActivity.class) );
            }
        });
		
	}
	
	
	public void goToDrilAppPage(View view){
		goToUrl ( "http://www.drilapp.com");
	}
	
	
	public void goWebPage(View view){
		goToUrl ( "http://www.peterjurkovic.sk/");
	}
	
	
	
	public void goToFbPage(View view){
		goToUrl ( "https://www.facebook.com/drilAnglictinaEfektivne");
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
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"email@peterjurkovic.sk"});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Dril");

		startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)) );
	}
	
	
	
}
