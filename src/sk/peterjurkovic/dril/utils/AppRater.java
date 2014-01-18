package sk.peterjurkovic.dril.utils;


import sk.peterjurkovic.dril.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class AppRater {
	
	    private final static int DAYS_UNTIL_PROMPT = 10;
	    private final static int LAUNCHES_UNTIL_PROMPT = 40;
	    
	    public static void app_launched(Context mContext) {
	        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
	        if (prefs.getBoolean("dontshowagain", false)) { return ; }
	        
	        SharedPreferences.Editor editor = prefs.edit();
	        
	        // Increment launch counter
	        long launch_count = prefs.getLong("launch_count", 0) + 1;
	        editor.putLong("launch_count", launch_count);

	        // Get date of first launch
	        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
	        if (date_firstLaunch == 0) {
	            date_firstLaunch = System.currentTimeMillis();
	            editor.putLong("date_firstlaunch", date_firstLaunch);
	        }
	        
	        // Wait at least n days before opening
	        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
	            if (System.currentTimeMillis() >= date_firstLaunch + 
	                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
	            	showRateDialog(mContext, editor);
	            }
	        }
	        editor.commit();
	    }   
	    
	    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    	builder.setTitle(R.string.rate_title);
	    	builder.setMessage(R.string.rate_text);
	    	builder.setPositiveButton(R.string.rate_now,  new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id){
            	 if (editor != null) {
	                    editor.putBoolean("dontshowagain", true);
	                    editor.commit();
	                }
            	 	GoogleAnalyticsUtils.logAction(mContext,
	            			GoogleAnalyticsUtils.CATEGORY_UI_ACTION, 
	            			GoogleAnalyticsUtils.ACTION_BUTTON_PRESS, "rate_now", 1l);
            	 			
            	 	mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
	                dialog.dismiss();
	            }
	       });
	        builder.setNeutralButton(R.string.rate_later, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	GoogleAnalyticsUtils.logAction(mContext,
	            			GoogleAnalyticsUtils.CATEGORY_UI_ACTION, 
	            			GoogleAnalyticsUtils.ACTION_BUTTON_PRESS, "rate_later", 0l);
	                dialog.dismiss();
	            }
	       }); 
	        
	       builder.setNegativeButton(R.string.rate_never, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) 
	            {
            	 if (editor != null) {
	                    editor.putBoolean("dontshowagain", true);
	                    GoogleAnalyticsUtils.logAction(mContext,
		            			GoogleAnalyticsUtils.CATEGORY_UI_ACTION, 
		            			GoogleAnalyticsUtils.ACTION_BUTTON_PRESS, "rate_never", -1l);
	                    editor.commit();
	             }
            	 dialog.dismiss();
	            }
	       }); 
	        
	        
	       AlertDialog alert = builder.create();
	       alert.show();
	    }

	    
	    
}
