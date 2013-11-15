package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 *
 */
public class BaseActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 ActionBar ab =  getSupportActionBar();
		ab.setIcon(R.drawable.dril_logo);
		ab.setTitle("");
		 
		 */
	}
	
	public void setGoHomePageListener(){
			View view = findViewById(R.id.drilPromo);
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
	
	
	protected void goToParentActivity(){
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
		Intent intent = null;
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	Log.i("BASE", "going back..");
		    	Intent prevActivityIntent = getIntent();
		    	Log.i("BASE", prevActivityIntent.toString());
			    if(prevActivityIntent != null && prevActivityIntent.getBooleanExtra(DrilActivity.DRIL_ID, false)){
			    	Log.i("BASE", "prev is DrilActivity");
			    	if(NavUtils.shouldUpRecreateTask(this, prevActivityIntent)){
			    		Log.i("BASE", "recreate ");
			    		TaskStackBuilder.create(this)
			    		.addNextIntentWithParentStack(prevActivityIntent)
			            .startActivities();
			    	}else{
			    		Log.i("BASE", "going up");
			    		prevActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    		finish();
			    	}
			    }else{
			    	goToParentActivity();
			    }
			    return true;
		    case R.id.settings:
				intent = new Intent(getApplicationContext(), PreferencesActivity.class);
				Log.i("BASE", this.getClass().getName());
				if(this.getClass().getName().equals(DrilActivity.class.getName())){
					intent.putExtra(DrilActivity.DRIL_ID, true);
				}
	    		startActivity(intent);
		        return true;
		    case R.id.feedback:
		    	Intent i = new Intent(this, FeedbackActivity.class);
        		startActivity(i);
		        return true;
			case R.id.startDril:
				intent = new Intent(getApplicationContext(), DrilActivity.class);
	    		startActivity(intent);
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
	
	  @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	    Log.i("GA", "onStart");
	  }
	
	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this); 
	    Log.i("GA", "onStop");
	  }
	  
	public void logException(String description, boolean fatal) {
		EasyTracker.getInstance(this).send(
				MapBuilder.createException(description, fatal)
				.build()
	    );
	}

}
