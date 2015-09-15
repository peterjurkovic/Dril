package sk.peterjurkovic.dril.v2.activities;

import static sk.peterjurkovic.dril.utils.StringUtils.isBlank;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DatabaseHelper;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.utils.validation.EmailValidator;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgottenPassowrdActivity extends BaseActivity {
	
	private final static String TAG = RegistrationActivity.class.getSimpleName();
	
	private Button sendBtn;
	private Button backBtn;
	private EditText emailField;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotten_password_layout);

		sendBtn = (Button) findViewById(R.id.send);
		backBtn = (Button) findViewById(R.id.btnBackToLogin);
		emailField = (EditText) findViewById(R.id.email);
		pDialog = new ProgressDialog(context);
		
		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!DeviceUtils.isDeviceOnline(context)){
            		Toast.makeText(getApplicationContext(),R.string.err_internet_conn, Toast.LENGTH_LONG).show();
            		return;
            	}
				final String email = emailField.getText().toString();
				if(isBlank(email) || !EmailValidator.getInstance().isValid(email)){
					Toast.makeText(context, R.string.err_email, Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					sendRequest( email );
				} catch (JSONException e) {
					GoogleAnalyticsUtils.logException(e, context);
				}
				
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(context, LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	
	
	private void sendRequest(final String email) throws JSONException{
		final JSONObject json = new JSONObject();
		json.put("username", email);
		json.put("deviceId", PreferenceManager.getDefaultSharedPreferences(context).getString(DatabaseHelper.DEVICE_ID, ""));
		json.put("deviceName", DeviceUtils.getDeviceInfo());
		
		showDialog();
        final JsonObjectRequest req = new JsonObjectRequest(Method.PUT, Api.FORGOTTEN_PASSOWRD, json,
        	
        		new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    	hideDialog();
                		showSuccessMessage(email);
                    }
                 },
                 new Response.ErrorListener() {
	                @Override
	                public void onErrorResponse(VolleyError error) {
	                	hideDialog();
	                	NetworkResponse res = error.networkResponse;
	                	if(res !=null && res.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
	                			Toast.makeText(context, 
	                					context.getString(R.string.email_not_found), 
	                					Toast.LENGTH_LONG
	                			).show();
	                			return;
	                	}
	                	Toast.makeText(context, R.string.err_account_create, Toast.LENGTH_LONG).show();	                    
	                }
            });
        
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, TAG);
	}
	
	public void showSuccessMessage(String email){
		TextView v = (TextView) findViewById(R.id.forrgottenPassDescr);
		v.setText(context.getString(R.string.forgotten_password_changed, email));
		sendBtn.setVisibility(View.GONE);
		emailField.setVisibility(View.GONE);
		((TextView) findViewById(R.id.yourEmailAddress))
			.setVisibility(View.GONE);
	}
	
	private void showDialog() {
	    if (!pDialog.isShowing()){
	        pDialog.show();
	    }   
	}
	 
	private void hideDialog() {
	    if (pDialog.isShowing()){
	        pDialog.dismiss();
	    }
	}
}
