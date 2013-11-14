package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.v2.adapters.BookAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 */
public class BookListActivity extends ActionBarListActivity {
	
	public static final String TAG = "BookListActivity";
	
	private static final int REQUEST_ADD_BOOK = 0;
	private static final int REQUEST_EDIT_BOOK = 1;
	
	public static final int MENU_EDIT_ID = Menu.FIRST+1;
	public static final int MENU_DELETE_ID = Menu.FIRST+2;
	
	private BookAdapter bookAdapter;
	private BookDBAdapter bookDBAdapter;
	
	protected ProgressBar booKProgressBar;
	protected ListView listView;
	protected TextView bookProgressBarLabel;
	protected TextView emptyList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_book_list_layout);
	    registerForContextMenu( getListView() );
	   
        booKProgressBar = (ProgressBar)findViewById(R.id.booKProgress);
        bookProgressBarLabel = (TextView)findViewById(R.id.booKProgressLabel);
        emptyList = (TextView)findViewById(android.R.id.empty);
        listView = (ListView)findViewById(android.R.id.list);
        bookDBAdapter = new BookDBAdapter(this);
        updateList();
	}
	
	private void showList(int countOfBooks){
		listView.setVisibility(View.VISIBLE);
		if( countOfBooks== 0){
			emptyList.setVisibility(View.VISIBLE);
		}
		booKProgressBar.setVisibility(View.GONE);
		bookProgressBarLabel.setVisibility(View.GONE);
	}
	private void showLoader(){
		listView.setVisibility(View.GONE);
		emptyList.setVisibility(View.GONE);
		booKProgressBar.setVisibility(View.VISIBLE);
		bookProgressBarLabel.setVisibility(View.VISIBLE);
	}
	
	public void updateList() {
		new LoadData(this).execute();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(Menu.NONE, MENU_EDIT_ID, Menu.NONE, R.string.edit);
	    menu.add(Menu.NONE, MENU_DELETE_ID, Menu.NONE, R.string.delete);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showLectureList(id);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case MENU_DELETE_ID:
        	deleteBook(info.id);
            return true;
        case MENU_EDIT_ID:
        	onEditBookClicked(info.id);
            return true;            
        default:
           return super.onContextItemSelected(item);
        }
	}
	
	
	
	public void deleteBook(long id){
        Boolean deleted = false;
        try {
        	deleted = bookDBAdapter.deleteBook(id);
		} catch (Exception e) {
			Log.e(TAG, "Can not delete book", e);
		}
	    
	    if(deleted){
	        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	        updateList();
	    } else{
	        Toast.makeText(this, R.string.book_not_deleted, Toast.LENGTH_SHORT).show();
	    }
	}
	
	
	@Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
	 	if(resultCode != RESULT_OK){
	 		return;
	 	} 
	 	switch(requestCode){
	 		case REQUEST_ADD_BOOK :
	 			onSaveNewBook();
	 		break;
	 		case REQUEST_EDIT_BOOK :
	 			onSaveEditedBook();
	 		break;
	 		default:
	 			throw new Error("requestCode: " + requestCode + 
	 							" is not implemented in onActivityResult");
	 	} 
        super.onActivityResult(requestCode, resultCode, data);
    }

	public void onAddBookClicked(View v){ 
		startAddBookActivity();
	}
	
	private void startAddBookActivity(){
		Intent i = new Intent(this, AddBookActivity.class);
		startActivityForResult(i, REQUEST_ADD_BOOK);
	}
	
	public void onSaveEditedBook() {	
		updateList();
		Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_LONG).show();
	}
	
	public void onEditBookClicked(long bookId) {
		Intent i = new Intent(this,  EditBookActivity.class);
		i.putExtra(EditBookActivity.EXTRA_BOOK_ID, bookId);
		startActivityForResult(i, REQUEST_EDIT_BOOK);
	}
	
	public void showLectureList(long id){
		Intent intent = new Intent(this, LectureListActivity.class);
		intent.putExtra( LectureListActivity.EXTRA_BOOK_ID , id);
		startActivity(intent);
	}
	
	public void deactiveAllCards(){
		WordDBAdapter wordDBAdapter = new WordDBAdapter(this);
		try {
			wordDBAdapter.deactiveAll();
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			wordDBAdapter.close();
		}
		updateList();
		Toast.makeText(this, R.string.words_deactived, Toast.LENGTH_LONG).show();		   
		
	}
	
	public void onSaveNewBook() {
		    updateList();
	}
	

	@Override
	protected void onDestroy() {
		closeAdapterCursor();
		super.onStop();
	}
	

	private void closeAdapterCursor(){
		try {
			if(bookAdapter != null){
				if(!bookAdapter.getCursor().isClosed())
					bookAdapter.getCursor().close();
			}
			if(bookDBAdapter != null)
					bookDBAdapter.close();
		} catch (Exception e) {
			Log.d(TAG, "closeAdapterCursor()");
		}
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
			if(cursor != null){
				int countOfBooks = cursor.getCount();
				bookAdapter = new BookAdapter(context, cursor, 0);
				setListAdapter(bookAdapter);
				bookAdapter.notifyDataSetChanged();
				showList(countOfBooks);
			}else{
				showList(0);
			}
		}
		
	}
	
	/*	ACTION BAR MENU ---------------------	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuAddBook :
				startAddBookActivity();
			return true;
			case R.id.menuDeactiveAll :
				deactiveAllCards();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.v2_book_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}
