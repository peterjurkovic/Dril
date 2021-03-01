package sk.peterjurkovic.dril.v2.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.sync.LogoutManager;
import sk.peterjurkovic.dril.sync.SyncManager;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 *
 */
public class BaseActivity extends AppCompatActivity {
	
	protected SessionManager session;
	protected Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		try {
	        ViewConfiguration config = ViewConfiguration.get(context);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	    	GoogleAnalyticsUtils.logException(e, context);
	    	Log.e(getClass().getSimpleName(), e.getMessage());
	    }
		session = new SessionManager(context);
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
	
	
	public void onLoginClick(){
		
	}
	
	
	protected void goToParentActivity(){
    	Intent upIntent =  NavUtils.getParentActivityIntent(this);
         if(NavUtils.shouldUpRecreateTask(this, upIntent)){
        	 TaskStackBuilder.create(this)
             .addNextIntentWithParentStack(upIntent)
             .startActivities();
         }else {
             if(upIntent == null){
            	 upIntent = new Intent(context, DashboardActivity.class);
             }
        	 upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             NavUtils.navigateUpTo(this, upIntent);
             finish();
         }
    }
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	Intent prevActivityIntent = getIntent();
			    if(prevActivityIntent != null && prevActivityIntent.getBooleanExtra(DrilActivity.DRIL_ID, false)){
			    	if(NavUtils.shouldUpRecreateTask(this, prevActivityIntent)){
			    		TaskStackBuilder.create(this)
			    		.addNextIntentWithParentStack(prevActivityIntent)
			            .startActivities();
			    	}else{
			    		prevActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    		finish();
			    	}
			    }else{
			    	goToParentActivity();
			    }
			    return true;
		    case R.id.settings:
				intent = new Intent(context, DrilPreferenceActivity.class);
				if(this.getClass().getName().equals(DrilActivity.class.getName())){
					intent.putExtra(DrilActivity.DRIL_ID, true);
				}
	    		startActivity(intent);
		        return true;
		    case R.id.feedback:
		    	Intent i = new Intent(context, FeedbackActivity.class);
        		startActivity(i);
		        return true;
			case R.id.startDril:
				intent = new Intent(context, DrilActivity.class);
	    		startActivity(intent);
		        return true;
			case R.id.sync :
				new SyncManager(context).execute();
				return true;
			case R.id.logout :
				new LogoutManager(context).execute();
				return true;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.v2_main, menu);
		if(session.isUserLoggedIn()){
			
		}else{
			menu.removeItem(R.id.sync);
			menu.removeItem(R.id.logout);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	
	  @Override
	  public void onStart() {
	    super.onStart();
	    GoogleAnalyticsUtils.create(this);
	  }

	

}
