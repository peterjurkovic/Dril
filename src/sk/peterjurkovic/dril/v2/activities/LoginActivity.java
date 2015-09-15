package sk.peterjurkovic.dril.v2.activities;


import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DatabaseHelper;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.sync.LoginManager;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginActivity extends BaseActivity{
	// LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private TextView forgottenPasswordButton;
    private EditText loginField;
    private EditText passwordField;
    private ProgressDialog pDialog;

 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.login);
 
        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        forgottenPasswordButton = (TextView) findViewById(R.id.forgotPass);
 
        // Progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
 
 
        // Check if user is already logged in or not
        if (session.isUserLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(context, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	if(!DeviceUtils.isDeviceOnline(context)){
            		Toast.makeText(getApplicationContext(),R.string.err_internet_conn, Toast.LENGTH_LONG).show();
            		return;
            	}
                String login = loginField.getText().toString();
                String password = passwordField.getText().toString();

                if (login.trim().length() > 4 && password.trim().length() > 5) {
                	long words = new WordDBAdapter(context).getCountOfStoredWords();
                	if(words > 0){
                    	showConfiramtionDialog(login, password);
                	}else{
            			login(login, password);
                	}
                } else {
                    Toast.makeText(getApplicationContext(),R.string.err_credentials, Toast.LENGTH_LONG).show();
                }
            }
            
            
            public void showConfiramtionDialog(final String login, final String password){
        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        		alertDialogBuilder
        			.setTitle(R.string.alert_login)
        			.setMessage(getString(R.string.alert_login_descr))
        			.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
        					@Override
        					public void onClick(DialogInterface dialog, int id) {
        						dialog.cancel();
        						login(login, password);
        					}
        			})
        			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        					@Override
                           public void onClick(DialogInterface dialog, int id) {
        						dialog.cancel();
                           }
        			});

        		AlertDialog alertDialog = alertDialogBuilder.create();
        		alertDialog.show();
        	}
 
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(context,RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });
        
        forgottenPasswordButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context,ForgottenPassowrdActivity.class);
                startActivity(i);
                finish();
			}
		});
 
    }
    
  
 
    /**
     * function to verify login details in mysql db
     * */
    private void login(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage(getApplicationContext().getString(R.string.logging_in));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final JSONObject jsonRequest = new JSONObject();
        try {
             jsonRequest.put("username", username);
             jsonRequest.put("password", password);
             jsonRequest.put("deviceId", preferences.getString(DatabaseHelper.DEVICE_ID, ""));
             jsonRequest.put("deviceName", DeviceUtils.getDeviceInfo());
		} catch (JSONException e1) {
			GoogleAnalyticsUtils.logException(e1, getApplicationContext());
		}
        
        Log.d(TAG, jsonRequest.toString());
        showDialog();
        JsonObjectRequest req = new JsonObjectRequest(Method.PUT, Api.LOGIN, jsonRequest,
        		new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
						new LoginManager(context, pDialog).execute(response);
                    }

                 },
                 new Response.ErrorListener() {
            	 
	                @Override
	                public void onErrorResponse(VolleyError error) {
	                	hideDialog();
	                	NetworkResponse res = error.networkResponse;
	                	if(res != null && res.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
	                		Toast.makeText(getApplicationContext(),R.string.err_http_401, Toast.LENGTH_LONG).show();
	                		return;
	                	}else if(res != null && res.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
	                		String message = StringUtils.extractError(res, context);
	                		if(message != null){
	                			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	                			return;
	                		}
	                	}
                		Log.e(TAG, "Login Error: " + error.getMessage());
	                    GoogleAnalyticsUtils.logException(error.getMessage(), false, getApplicationContext());
	                    Toast.makeText(getApplicationContext(),R.string.login_failed, Toast.LENGTH_LONG).show();
	                				                    
	                }
            });
        
        req.setRetryPolicy(new DefaultRetryPolicy(8000, 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_string_req);
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
