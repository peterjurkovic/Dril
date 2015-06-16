package sk.peterjurkovic.dril.v2.activities;

import static sk.peterjurkovic.dril.utils.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.AppController;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DatabaseHelper;
import sk.peterjurkovic.dril.dto.State;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.SpinnerState;
import sk.peterjurkovic.dril.utils.DeviceUtils;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.validation.EmailValidator;
import sk.peterjurkovic.dril.v2.constants.Api;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

public class RegistrationActivity extends BaseActivity {
	
	private final static String TAG = RegistrationActivity.class.getSimpleName();
	private final static String ENCODING = "utf-8";
	
	
	private EditText loginField;
	private EditText emailField;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private Spinner localeSpinner;
	private Spinner targetLocaleSpinner;
	private EditText firstNameField;
	private EditText lastNameField;
	
	private Button createAccount;
	private Button login;
	
	private ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		
		
		loginField = (EditText) findViewById(R.id.login_field);
		emailField = (EditText) findViewById(R.id.email_field);
		passwordField = (EditText) findViewById(R.id.password_field);
		confirmPasswordField = (EditText) findViewById(R.id.confirm_password_field);
		localeSpinner = (Spinner) findViewById(R.id.locale_field);
		targetLocaleSpinner = (Spinner) findViewById(R.id.target_locale_field);
		firstNameField = (EditText) findViewById(R.id.first_name_field);
		lastNameField = (EditText) findViewById(R.id.last_name_field);
		
		ArrayAdapter<State> adapter = languageAdapter();
		localeSpinner.setAdapter(adapter);
		targetLocaleSpinner.setAdapter(adapter);
		
		createAccount = (Button) findViewById(R.id.register);
		login = (Button) findViewById(R.id.btnBackToLogin);
		
		login.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view) {
            		Intent i = new Intent(context, LoginActivity.class);
            		startActivity(i);
            		finish();
            }
        });
		
		createAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
            	if(!DeviceUtils.isDeviceOnline(context)){
            		Toast.makeText(getApplicationContext(),R.string.err_internet_conn, Toast.LENGTH_LONG).show();
            		return;
            	}
				try {
					createAccount();
				} catch (JSONException e) {
					GoogleAnalyticsUtils.logException(e, context);
				}
			}
		});
		pDialog = new ProgressDialog(context);
	    pDialog.setCancelable(false);
	    pDialog.setMessage(context.getString(R.string.creating_account));
	}
	
	private void createAccount() throws JSONException {
		String login = loginField.getText().toString();
		if(isBlank(login) || login.length() < 5){
			showMessage(R.string.err_login);	
			return;
		}
		String email = emailField.getText().toString();
		if(isBlank(email) || !EmailValidator.getInstance().isValid(email)){
			showMessage(R.string.err_email);
			return;
		}
		String pass = passwordField.getText().toString();
		if(isBlank(pass) || pass.length() < 6){
			showMessage(R.string.err_pass);
			return;
		}
		
		String confirmPass = confirmPasswordField.getText().toString();
		if(isBlank(confirmPass) || confirmPass.length() < 6 || !confirmPass.equals(pass)){
			showMessage(R.string.err_confir_pass);
			return;
		}
		
		long localeId = ((State)localeSpinner.getSelectedItem()).getId();
		long targetLocaleId = ((State)targetLocaleSpinner.getSelectedItem()).getId();
		
		
		String firstName = firstNameField.getText().toString();
		if(isBlank(firstName) || firstName.length() < 1){
			showMessage(R.string.err_first_name);
			return;
		}
		
		String lastName = lastNameField.getText().toString();
		if(isBlank(lastName) || lastName.length() < 1){
			showMessage(R.string.err_last_name);
			return;
		}
		JSONObject req = new JSONObject();
		req.put("login", login);
		req.put("email", email);
		req.put("password", pass);
		req.put("password2", confirmPass);
		req.put("localeId", localeId);
		req.put("targetLocaleId", targetLocaleId);
		req.put("firstName", lastName);
		req.put("lastName", lastName);
		sendRequest(req);
	}
	
	
	private void sendRequest(JSONObject json) throws JSONException{
		json.put("deviceId", PreferenceManager.getDefaultSharedPreferences(context).getString(DatabaseHelper.DEVICE_ID, ""));
		json.put("deviceName", DeviceUtils.getDeviceInfo());
		
		showDialog();
        JsonObjectRequest req = new JsonObjectRequest(Method.POST, Api.REGISTRATION, json,
        		new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    	hideDialog();
                    }

                 },
                 new Response.ErrorListener() {
            	 
	                @Override
	                public void onErrorResponse(VolleyError error) {
	                	hideDialog();
	                	NetworkResponse res = error.networkResponse;
	                	if(res !=null && res.statusCode == HttpURLConnection.HTTP_BAD_REQUEST){
	                		try {
								JSONObject jsonRes = new JSONObject(new String(res.data, HttpHeaderParser.parseCharset(res.headers, ENCODING)));
								if(jsonRes.has("error")){
									JSONObject jsonError = jsonRes.getJSONObject("error");
									String errorMessage = jsonError.getString("message");
									Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
									return;
								}
							} catch (Exception e) {
								GoogleAnalyticsUtils.logException(e, context);
							}
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
	
	private void showMessage(int res){
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}
	
	protected ArrayAdapter<State> languageAdapter(){
		return getAddpter( Language.getAllStates() );
	}
	
	protected ArrayAdapter<State> getAddpter(List<SpinnerState> itemList){
		List<State> list = new ArrayList<State>(itemList.size());
		for(SpinnerState lang : itemList){
			list.add( new State(lang.getId(), context.getResources().getString(lang.getResource())));
		}
		ArrayAdapter<State> adapter =  new ArrayAdapter<State>(this,  R.layout.v2_spinner,  list);
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
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
