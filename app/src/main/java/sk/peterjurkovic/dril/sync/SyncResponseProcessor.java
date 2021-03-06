package sk.peterjurkovic.dril.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;

public class SyncResponseProcessor extends AsyncTask<JSONObject, Void, Boolean> {

	private final SyncDbAdapter dbAdapter;
	private final Context context;
	private final boolean showNotifications;
	
	public SyncResponseProcessor(final Context context, final SyncDbAdapter dbAdapter, boolean shotNotification){
		this.context = context;
		this.dbAdapter = dbAdapter;
		this.showNotifications = shotNotification;
	}
	
	
	@Override
	protected Boolean doInBackground(JSONObject... response) {
		return dbAdapter.sync(response[0]);
	}
	
	@Override
	protected void onPostExecute(Boolean isSuccessfull) {
		if(isSuccessfull){
			if(showNotifications){
				Toast.makeText(context,R.string.synced, Toast.LENGTH_SHORT).show();
			}
			if(context instanceof OnSuccessSyncListener){
				((OnSuccessSyncListener)context).onSuccessSync();
			}
		}else{
			if(showNotifications){
				Toast.makeText(context,R.string.sync_failed, Toast.LENGTH_LONG).show();
			}
		}
	}

}
