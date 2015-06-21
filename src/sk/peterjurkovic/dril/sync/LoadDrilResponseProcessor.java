package sk.peterjurkovic.dril.sync;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.SyncDbAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoadDrilResponseProcessor extends AsyncTask<JSONObject, Void, Void> {
	
	private final ProgressDialog dialog;
	private final Context context;
	
	public LoadDrilResponseProcessor(final Context context, final ProgressDialog dialog){
		this.context = context;
		this.dialog = dialog;
	}


	@Override
	protected void onPreExecute() {
		dialog.setMessage(context.getString(R.string.importing_data));
	}
	
	@Override
	protected Void doInBackground(JSONObject... response) {
		if(response[0].has("bookList")){
			new SyncDbAdapter(context).saveData(response[0]);
		}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Void result ) {
		hideDialog();
	}
	
	
    private void hideDialog() {
        if (dialog.isShowing()){
        	dialog.dismiss();
        }
    }
}
