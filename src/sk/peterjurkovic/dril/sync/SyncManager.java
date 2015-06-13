package sk.peterjurkovic.dril.sync;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

	public SyncManager(final Context context){
		this.context = context;
		this.dbAdapter = new SyncDbAdapter(context);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(!DeviceUtils.isDeviceOnline(context)){
    		Toast.makeText(context,R.string.err_internet_conn, Toast.LENGTH_LONG).show();
    		cancel(true);
    		return;
    	}
		Toast.makeText(context,R.string.syncing, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(context,R.string.sync_failed, Toast.LENGTH_LONG).show();
				return;
			}
			JsonObjectRequest req = new JsonObjectRequest(Method.POST, Api.SYNC, request,
					new Response.Listener<JSONObject>() {
				
				        @Override
				        public void onResponse(JSONObject response) {
				            Log.d(TAG, "Sync Response: " + response.toString());
				            new SyncResponseProcessor(context, dbAdapter).execute(response);
				        }
				 },
				 new Response.ErrorListener() {
		            @Override
		            public void onErrorResponse(VolleyError error) {
		            	NetworkResponse networkResponse = error.networkResponse;
		            	if(networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
		            		Toast.makeText(context,R.string.err_http_401, Toast.LENGTH_LONG).show();
		            	}else{
		            		Toast.makeText(context,R.string.sync_failed, Toast.LENGTH_LONG).show();
		            	}
		                Log.e(TAG, "Login Error: " + error.getMessage());
		            
		            
		            }
		        });
				
				req.setRetryPolicy(new DefaultRetryPolicy(8000, 
		                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
		                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
				AppController.getInstance().addToRequestQueue(req, TAG);
	}
}
