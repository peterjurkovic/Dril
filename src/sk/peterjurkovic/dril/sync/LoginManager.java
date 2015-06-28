package sk.peterjurkovic.dril.sync;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import sk.peterjurkovic.dril.io.DrilBackup;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.activities.DashboardActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class LoginManager extends AsyncTask<JSONObject, Void, Boolean>{

	private final Context context;
	private final ProgressDialog pDialog;
	
	public LoginManager(final Context context, final ProgressDialog dialog){
		this.context = context;
		this.pDialog = dialog; 
	}
	
	@Override
	protected void onPreExecute() {
		pDialog.setMessage(context.getString(R.string.importing_data));
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(JSONObject... response) {
		try{
			new DrilBackup(context).processBackup(true);
		}catch(Exception e){
			GoogleAnalyticsUtils.logException(e, context);
		}
		SyncDbAdapter dbAdapter = new SyncDbAdapter(context);
		return dbAdapter.processLogin(response[0]);
	}
	
	
	@Override
	protected void onPostExecute(Boolean isSuccessfullyLoggedIn) {
		super.onPostExecute(isSuccessfullyLoggedIn);
		pDialog.dismiss();
		if(isSuccessfullyLoggedIn){
			Toast.makeText(context, R.string.login_success, Toast.LENGTH_LONG).show();
			Intent i = new Intent(context, DashboardActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}else{
			Toast.makeText(context, R.string.login_failed, Toast.LENGTH_LONG).show();
		}
		
	}

}
