package sk.peterjurkovic.dril.utils;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

public class GoogleAnalyticsUtils {

	
	
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
}
