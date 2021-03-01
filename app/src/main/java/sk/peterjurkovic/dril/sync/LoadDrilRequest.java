package sk.peterjurkovic.dril.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Api;

public class LoadDrilRequest {
	
	private final static String TAG = LoadDrilRequest.class.getSimpleName();
	
	private final Context context;
	private final SessionManager session;
	private final ProgressDialog dialog;
	
	public LoadDrilRequest(Context context, SessionManager sessionManager){
		this.context = context;
		this.session = sessionManager;
		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
	}
	
	public void send(){
		if(DeviceUtils.isDeviceOnline(context)){
			JSONObject request = new JSONObject();
			try {
				request.put("localeId", session.getLocaleId());
				request.put("targetLocaleId", session.getTargetLocaleId());
				request.put("deviceId", DeviceUtils.getDeviceInfo());
				send(request);
			} catch (Exception e) {
				GoogleAnalyticsUtils.logException(e);
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
			
		}
	}
	
	
	private void send(JSONObject request){
		 dialog.show();
		 final JsonObjectRequest req = new JsonObjectRequest(Method.POST, Api.INIT_DRIL, request,
	 		new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
					new LoadDrilResponseProcessor(context, dialog).execute(response);
                }
             },
             new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                	hideDialog();
                	if(error.networkResponse != null){
						GoogleAnalyticsUtils.logError("load_dril", error);
                	}
                    Toast.makeText(context,R.string.err_data_load, Toast.LENGTH_LONG).show();		                    
                }
        });
    
	    req.setRetryPolicy(new DefaultRetryPolicy(8000,
	            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
	            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	    
	    AppController.getInstance().addToRequestQueue(req, TAG);
	}
	
	
	private void hideDialog() {
        if (dialog.isShowing()){
        	dialog.dismiss();
        }
    }
}
