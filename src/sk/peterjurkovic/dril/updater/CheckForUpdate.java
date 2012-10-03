package sk.peterjurkovic.dril.updater;

import java.net.UnknownHostException;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.listener.AsyncLIstener;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Check for new available book in remote server, Do it in background using AsyncTask
 * 
 * @author peto
 *
 */
public class CheckForUpdate extends AsyncTask<String, Integer, Integer> {
	
	private AsyncLIstener listener;
	private Context context;
	private ProgressDialog dialog;
	
	
	public CheckForUpdate(Context c){
		context = c;
		dialog = ProgressDialog.show( context , "" , 
				context.getResources().getString(R.string.loading_check), true);
		try {
			listener = (AsyncLIstener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(" must implement AsyncLIstener");
        }
	}
	
	@Override
	protected void onPreExecute() {
		dialog.show();
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		dialog.hide();
		Log.d("CFA", "onPostExecute: " + result);
		listener.onCheckResponse(result);
	}
	
	
	@Override
	protected Integer doInBackground(String... arg0) {
		int count = -1;
		try {
			DBAdapter db = new DBAdapter(context);
			long lastVer = db.getLastVersionOfTextbooks();
			JSONReciever jsonReciever = new JSONReciever( lastVer );
			JSONParser jsonParser = new JSONParser();
			count = jsonParser.getCountOfNewBooks( 
					jsonReciever.getJSONData( JSONReciever.FOR_CHECK_ACTION )
					);
			Log.d("MA", "json response: " + count);
		
		
		} catch (Exception e) {
			count = -1;
		}
		
		return count;
	}
	
	
}
