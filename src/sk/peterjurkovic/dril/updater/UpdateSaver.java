package sk.peterjurkovic.dril.updater;

import java.util.List;

import org.json.JSONObject;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.listener.AsyncLIstener;
import sk.peterjurkovic.dril.model.Book;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateSaver extends AsyncTask<String, Integer, Integer> {
	
	private AsyncLIstener listener;
	private Context context;
	private ProgressDialog dialog;
	
	
	public UpdateSaver(Context c){
		context = c;
		dialog = ProgressDialog.show( context , "" , 
				context.getResources().getString(R.string.loading_down), true);
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
		Log.d("UD", "onPostExecute: " + result);
		listener.onUpdatedResponse(result);
	}
	
	
	@Override
	protected Integer doInBackground(String... arg0) {
		try {
			DBAdapter db = new DBAdapter(context);
			long lastVer = db.getLastVersionOfTextbooks();
			JSONReciever jsonReciever = new JSONReciever( lastVer );
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = jsonReciever.getJSONData( JSONReciever.FOR_UPDATE_ACTION );
			List<Book> books = jsonParser.parseBooks(jsonObject);
			db.updateBooks(books);
			return books.size();
		} catch (Exception e) {
			return -1;
		}
	}
}
