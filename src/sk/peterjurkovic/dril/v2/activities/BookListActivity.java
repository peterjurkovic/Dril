package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.v2.adapters.BookAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookListActivity extends ActionBarListActivity {
	
	public static final String TAG = "BookListActivity";
	
	private static final int REQUEST_ADD_BOOK = 0;
	private static final int REQUEST_EDIT_BOOK = 1;
	
	public static final int MENU_VIEW_ID = Menu.FIRST +1;
	public static final int MENU_EDIT_ID = Menu.FIRST+2;
	public static final int MENU_DELETE_ID = Menu.FIRST+3;
	
	BookAdapter bookAdapter;
	BookDBAdapter bookDBAdapter;
	
	protected ProgressBar booKProgressBar;
	protected ListView listView;
	protected TextView bookProgressBarLabel;
	protected TextView emptyList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_book_list_activity);
	    registerForContextMenu( getListView() );
	   
        booKProgressBar = (ProgressBar)findViewById(R.id.booKProgress);
        bookProgressBarLabel = (TextView)findViewById(R.id.booKProgressLabel);
        emptyList = (TextView)findViewById(android.R.id.empty);
        listView = (ListView)findViewById(android.R.id.list);
        bookDBAdapter = new BookDBAdapter(this);
        updateList();
	}
	
	protected void showList(){
		listView.setVisibility(View.VISIBLE);
		emptyList.setVisibility(View.VISIBLE);
		booKProgressBar.setVisibility(View.GONE);
		bookProgressBarLabel.setVisibility(View.GONE);
	}
	protected void showLoader(){
		listView.setVisibility(View.GONE);
		emptyList.setVisibility(View.GONE);
		booKProgressBar.setVisibility(View.VISIBLE);
		bookProgressBarLabel.setVisibility(View.VISIBLE);
	}
	
	public void updateList() {
		new LoadData(this).execute();
	}
	
	/* LOADING CURSOR DATA IN BACKGROUND -------------- */
	private class LoadData extends AsyncTask<Void, Void, Cursor>{
		
		Context context;
		
		public LoadData(Context context){
			this.context = context;
		}
		@Override
		protected void onPreExecute(){     
			showLoader();
		}
		
		@Override
		protected Cursor doInBackground(Void... params) {
			Cursor cursor = null;
			try {
				cursor = bookDBAdapter.getBooks();
			} catch (Exception e) {
				Log.e(TAG, "Can not retrieve books", e);
			}
			return cursor;
		}
		
		@Override
        protected void onPostExecute(Cursor cursor){
			bookAdapter = new BookAdapter(context, cursor, 0);
			setListAdapter(bookAdapter);
			bookAdapter.notifyDataSetChanged();
			showList();
		}
		
	}
	
}
