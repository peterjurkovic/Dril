package sk.peterjurkovic.dril.utils;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 28, 2013
 *
 */
public class GoogleAnalyticsUtils {

	public static final String CATEGORY_UI_ACTION = "ui_action";
	public static final String CATEGORY_PROCESSING_ACTION = "processing_action";
	
	public static final String ACTION_BUTTON_PRESS = "button_press";
	public static final String ACTION_RESULT = "process_result";
	
	public static void logException(final Exception e, final Context context){
		 if(e != null && context != null){
			 EasyTracker easyTracker = EasyTracker.getInstance(context);
			 easyTracker.send(MapBuilder
				      .createException(new StandardExceptionParser(context, null)             
				      .getDescription(Thread.currentThread().getName(),  e),  false)                                              
				      .build()
			 );
		 }
	}
	
	public static void logException(final String message, final boolean fatal, final Context context){
		if(context != null && StringUtils.isNoneBlank(message)){
			EasyTracker.getInstance(context).send(
					MapBuilder.createException(message, fatal)
					.build()
		    );
		}
	}
	
	
	
	public static void logAction(Context context, final String category, final String action, final String label, final Long value){
		EasyTracker tracker = EasyTracker.getInstance(context);
  		tracker.send(MapBuilder.createEvent(
  				category, 
  				action, 
  				label, 
  				value)
  				.build());
	}
}
