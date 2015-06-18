package sk.peterjurkovic.dril.sync;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class SyncManager extends AsyncTask<Void, Void, JSONObject>{
	
	private final static String TAG = SyncManager.class.getSimpleName();
	
	private final Context context;
	private final SyncDbAdapter dbAdapter;
	private final boolean showNotifications;

	public SyncManager(final Context context){
		this(context, true);
	}
	
	public SyncManager(final Context context, final boolean showNotification){
		this.context = context;
		this.dbAdapter = new SyncDbAdapter(context);
		this.showNotifications = showNotification;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(!DeviceUtils.isDeviceOnline(context)){
			showNotification(R.string.err_internet_conn);
    		cancel(true);
    		return;
    	}
		showNotification(R.string.syncing);
	}	

	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			return  dbAdapter.getSyncRequest();
		} catch (JSONException e) {
			GoogleAnalyticsUtils.logException(e, context);
		}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(JSONObject request) {
			if(request == null){
				showNotification(R.string.sync_failed);
				return;
			}
			JsonObjectRequest req = new JsonObjectRequest(Method.POST, Api.SYNC, request,
			
				new Response.Listener<JSONObject>() {
				
				        @Override
				        public void onResponse(JSONObject response) {
				            Log.d(TAG, "Sync Response: " + response.toString());
				            new SyncResponseProcessor(context, dbAdapter, showNotifications).execute(response);
				        }
				 },
				 
				 new Response.ErrorListener() {
		            @Override
		            public void onErrorResponse(VolleyError error) {
		            	NetworkResponse networkResponse = error.networkResponse;
		            	if(networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
		            		showNotification(R.string.err_http_401);
		            	}else{
		            		showNotification(R.string.sync_failed);
		            	}
		                Log.e(TAG, "Login Error: " + error.getMessage());
		            
		            
		            }
		        }){
				
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					final SessionManager session = new SessionManager(context);
					final String token = session.getToken();
					final Map<String, String> headers = new HashMap<String, String>(1);
					headers.put("Authorization", "Bearer " + token);
					return headers;
				}				
			};
				
				req.setRetryPolicy(new DefaultRetryPolicy(8000, 
		                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
		                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
				AppController.getInstance().addToRequestQueue(req, TAG);
	}
	
	private void showNotification(int resource){
		if(showNotifications){
			Toast.makeText(context, resource, Toast.LENGTH_LONG).show();
		}
	}
}
