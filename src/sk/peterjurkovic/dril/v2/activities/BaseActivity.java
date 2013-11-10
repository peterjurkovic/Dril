package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 *
 */
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
	
	
	protected void gotBack(){
    	Intent upIntent =  NavUtils.getParentActivityIntent(this);
         if(NavUtils.shouldUpRecreateTask(this, upIntent)){
        	 TaskStackBuilder.create(this)
             .addNextIntentWithParentStack(upIntent)
             .startActivities();
         }else {
             upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             NavUtils.navigateUpTo(this, upIntent);
         }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	       gotBack();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.v2_main, menu);
			return super.onCreateOptionsMenu(menu);
		}
}
