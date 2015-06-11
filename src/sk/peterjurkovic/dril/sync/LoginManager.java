package sk.peterjurkovic.dril.sync;

import org.json.JSONObject;

import sk.peterjurkovic.dril.db.SyncDbAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoginManager extends AsyncTask<JSONObject, Void, Boolean>{

	private final Context context;
	private final ProgressDialog pDialog;
	
	public LoginManager(final Context context, final ProgressDialog dialog){
		this.context = context;
		this.pDialog = dialog; 
	}
	
	@Override
	protected void onPreExecute() {
		pDialog.setMessage("Importing data..");
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(JSONObject... response) {
		SyncDbAdapter dbAdapter = new SyncDbAdapter(context);
		return dbAdapter.processLogin(response[0]);
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		pDialog.hide();
		super.onPostExecute(result);
	}

}
