package sk.peterjurkovic.dril.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import okhttp3.Request;
import okhttp3.Response;


public class GoogleAnalyticsUtils {

	public static final String CATEGORY_UI_ACTION = "ui_action";
	public static final String CATEGORY_PROCESSING_ACTION = "processing_action";

	public static final String ACTION_BUTTON_PRESS = "button_press";
	public static final String ACTION_RESULT = "process_result";


    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void create(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }
	public static void logException(final Exception e, final Context context){
		if(e != null && context != null){
			FirebaseCrashlytics.getInstance().recordException(e);
		}
	}

	public static void logException(final Exception e){
		if(e != null){
			FirebaseCrashlytics.getInstance().recordException(e);
		}
	}

	public static void logException(Object ctx, final Exception e){
		if(e != null){
			Log.e(ctx.getClass().getSimpleName(), e.getMessage());
			FirebaseCrashlytics.getInstance().recordException(e);
		}
	}


	public static void log(Request request, Response response){
		if (mFirebaseAnalytics != null && response != null){
			Bundle bundle = new Bundle();
			bundle.putString("method", request.method());
			bundle.putString("url", request.url().toString());
			bundle.putInt("status", response.code());
			bundle.putLong("duration_ms", response.sentRequestAtMillis() - response.receivedResponseAtMillis());
			mFirebaseAnalytics.logEvent("request", bundle);
		}
	}

	public static void logError(String tag, VolleyError error ){
		if (mFirebaseAnalytics != null && error != null && error.networkResponse != null){
			Bundle bundle = new Bundle();
			bundle.putString("tag", tag);
			bundle.putLong("network_time_ms", error.getNetworkTimeMs());
			bundle.putLong("status_code", error.networkResponse.statusCode);
			mFirebaseAnalytics.logEvent("network_error", bundle);
		}
	}

	public static void log(final Exception e){
		if(e != null){
			FirebaseCrashlytics.getInstance().recordException(e);
		}
	}

	public static void logException(final Exception e, final String message, final boolean fatal, final Context context){
		if(context != null && message != null){
			FirebaseCrashlytics.getInstance().log(message);
		}
	}



	public static void logAction(Context context, final String category, final String action, final String label, final Long value){
		if (mFirebaseAnalytics != null){
			Bundle bundle = new Bundle();
			bundle.putString("category", category);
			bundle.putLong("value", value);
			bundle.putString("label", label);
			mFirebaseAnalytics.logEvent(action, bundle);
		}
	}




}
