package sk.peterjurkovic.dril.sync;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class SyncManager {
	
	private final static String TAG = "syncRequest";
	
	public void peformSync(final Context context){
		SyncDbAdapter db = new SyncDbAdapter(context);
		JSONObject request = null;
		try {
			request = db.getSyncRequest();
		} catch (JSONException e1) {
			e1.printStackTrace();
			return;
		}
		
		JsonObjectRequest req = new JsonObjectRequest(Method.POST, Api.SYNC, request,
			new Response.Listener<JSONObject>() {
		
		        @Override
		        public void onResponse(JSONObject response) {
		            Log.d(TAG, "Login Response: " + response.toString());
		   
		
		        try {
					if(response.get("error") == null){
					
				}else{
					Toast.makeText(context,"Login res error: " , Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		    }
		    
		    
		 },
		 new Response.ErrorListener() {
		            @Override
		            public void onErrorResponse(VolleyError error) {
		            	NetworkResponse networkResponse = error.networkResponse;
		            	if(networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
		            		Toast.makeText(context,R.string.err_http_401, Toast.LENGTH_LONG).show();
		            	}
		                Log.e(TAG, "Login Error: " + error.getMessage());
		            
		            
		        }
        });
		AppController.getInstance().addToRequestQueue(req, TAG);
	}
}
