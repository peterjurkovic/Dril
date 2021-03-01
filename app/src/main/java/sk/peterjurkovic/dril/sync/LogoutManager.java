package sk.peterjurkovic.dril.sync;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import sk.peterjurkovic.dril.io.DrilRestore;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.v2.activities.LoginActivity;

public class LogoutManager extends AsyncTask<Void, Void, Void>{
	
	private final Context context;
	
	public LogoutManager(final Context context){
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		final SyncDbAdapter manager = new SyncDbAdapter(context);
		manager.processLogout();
		try{
			new DrilRestore(context).processRestore(true);
		}catch(Exception e){
			GoogleAnalyticsUtils.logException(e, context);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		Toast.makeText(context, R.string.logout_success, Toast.LENGTH_LONG).show();
		Intent i = new Intent(context, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
}
