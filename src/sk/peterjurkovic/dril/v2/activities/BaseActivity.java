package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class BaseActivity extends ActionBarActivity {

	
	
	public void setGoHomePageListener(){
			View view = (View) findViewById(R.id.drilPromo);
			if(view != null){
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						goToHomePage(v);
					}
				}
		       );
			}
	}
	
	public void goToHomePage(View view){
		Uri uriUrl = Uri.parse(Constants.DRIL_HOMEPAGE_URL);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
	}
}
