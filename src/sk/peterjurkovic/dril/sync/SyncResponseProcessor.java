package sk.peterjurkovic.dril.sync;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SyncResponseProcessor extends AsyncTask<JSONObject, Void, Boolean> {

	private final SyncDbAdapter dbAdapter;
	private final Context context;
	
	public SyncResponseProcessor(final Context context, SyncDbAdapter dbAdapter){
		this.context = context;
		this.dbAdapter = dbAdapter;
	}
	
	
	@Override
	protected Boolean doInBackground(JSONObject... response) {
		return dbAdapter.sync(response[0]);
	}
	
	@Override
	protected void onPostExecute(Boolean isSuccessfull) {
		if(isSuccessfull){
			Toast.makeText(context,R.string.synced, Toast.LENGTH_SHORT).show();
			if(context instanceof OnSuccessSyncListener){
				((OnSuccessSyncListener)context).onSuccessSync();
			}
		}else{
			Toast.makeText(context,R.string.sync_failed, Toast.LENGTH_LONG).show();
		}
	}

}
