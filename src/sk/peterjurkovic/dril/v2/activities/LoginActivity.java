package sk.peterjurkovic.dril.v2.activities;


import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.SessionManager;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginActivity extends NetworkActivity{
	// LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText loginField;
    private EditText passwordField;
    private ProgressDialog pDialog;
    private SessionManager session;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
 
        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
 
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
 
        // Session manager
        session = new SessionManager(getApplicationContext());
 
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	if(!isOnline()){
            		Toast.makeText(getApplicationContext(),R.string.err_internet_conn, Toast.LENGTH_LONG).show();
            		return;
            	}
                String login = loginField.getText().toString();
                String password = passwordField.getText().toString();

                if (login.trim().length() > 4 && password.trim().length() > 5) {
                    login(login, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),R.string.err_credentials, Toast.LENGTH_LONG).show();
                }
            }
 
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                //Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                //startActivity(i);
                //finish();
            }
        });
 
    }
 
    /**
     * function to verify login details in mysql db
     * */
    private void login(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
 
        pDialog.setMessage("Logging in ...");
        
        final JSONObject jsonRequest = new JSONObject();
        try {
             jsonRequest.put("username", username);
             jsonRequest.put("password", password);
             jsonRequest.put("type", "dril");
		} catch (JSONException e1) {
			
		}
        
        Log.d(TAG, jsonRequest.toString());
        showDialog();
        JsonObjectRequest req = new JsonObjectRequest(Method.PUT, Api.LOGIN, jsonRequest,
        		new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
							if(response.get("error") == null){
								
							}else{
								Toast.makeText(getApplicationContext(),"Login res error: " , Toast.LENGTH_LONG).show();
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
			                	hideDialog();
			                	NetworkResponse networkResponse = error.networkResponse;
			                	if(networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED){
			                		Toast.makeText(getApplicationContext(),R.string.err_http_401, Toast.LENGTH_LONG).show();
			                	}
			                    Log.e(TAG, "Login Error: " + error.getMessage());
			                    
			                    
			                }
            });
        

        
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 
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
