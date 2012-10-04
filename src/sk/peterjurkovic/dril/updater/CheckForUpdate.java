package sk.peterjurkovic.dril.updater;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.listener.AsyncLIstener;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

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
	
	public static final int STATE_NO_UPDATE = 0;
	public static final int STATE_PARSING_ERROR = -1;
	public static final int STATE_NO_INTERNET_CONN = -2;
	
	
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
		//Log.d("CFA", "onPostExecute: " + result);
		listener.onCheckResponse(result);
	}
	
	
	@Override
	protected Integer doInBackground(String... arg0) {
		if(!isOnline()){
			return STATE_NO_INTERNET_CONN;
		}
		try {
			DBAdapter db = new DBAdapter(context);
			long lastVer = db.getLastVersionOfTextbooks();
			JSONReciever jsonReciever = new JSONReciever( lastVer );
			JSONParser jsonParser = new JSONParser();
			return jsonParser.getCountOfNewBooks( 
					jsonReciever.getJSONData( JSONReciever.FOR_CHECK_ACTION )
					);
		} catch (Exception e) {
			return STATE_PARSING_ERROR;
		}
		
	}
	
	public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

	
}
