package sk.peterjurkovic.dril;

import java.util.List;

import sk.peterjurkovic.dril.db.DBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.updater.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class UpdateActivity extends Activity {
	
	public static String RESULT_MSG_KEY = "rmsg";
	
	ProgressDialog dialog;
	
	private String resultMessage;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.update_activity);
		//RemoteUpdate updater = new RemoteUpdate(this);
		//updater.execute();
		
		//Intent result = new Intent();
		//result.putExtra(RESULT_MSG_KEY, resultMessage);
		//setResult(RESULT_OK, result);
		//finish();
	}
	/*
	
	private class RemoteUpdate extends AsyncTask<String, Integer, String> {
		
		private Context context;
		
		public RemoteUpdate(Context c){
			context = c;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			List<Book> books = null;
			try {
				DBAdapter db = new DBAdapter(context);
				JSONParser json = new JSONParser();
				books = json.parseBooks();
				if(books.size() == 0)
					return "Your textbooks are up to date.";
				db.updateBooks( books  );
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "Successfuly updated! Count of new textbooks: " + books.size();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(getApplicationContext()
						, "", "Updating. Please wait...", true);
			
		}
		
		 @Override
		    protected void onPostExecute(String result) {
			 resultMessage = result;
			 Log.d("UA", resultMessage);
			 dialog.cancel();
		    }
		
	}
	*/
	
	
}
