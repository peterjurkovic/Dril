package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.adapter.BookAdapter;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class BookListActivity extends ListActivity {
	
	public static final String TAG = "BookListActivity";
	
	private static final int REQUEST_ADD_BOOK = 0;
	private static final int REQUEST_EDIT_BOOK = 1;
	
	public static final int MENU_VIEW_ID = Menu.FIRST +1;
	public static final int MENU_EDIT_ID = Menu.FIRST+2;
	public static final int MENU_DELETE_ID = Menu.FIRST+3;
	
	BookAdapter bookAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.book_list_activity);
	    registerForContextMenu( getListView() );
	    ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(BookListActivity.this, DashboardActivity.class) );
            }
        });
        updateList();
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
        case MENU_VIEW_ID:
        		showLectureList(info.id);
        return true;
            
        default:
                return super.onContextItemSelected(item);
        }
	}
		
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(Menu.NONE, MENU_VIEW_ID, Menu.NONE, R.string.view);
	    menu.add(Menu.NONE, MENU_EDIT_ID, Menu.NONE, R.string.edit);
	    menu.add(Menu.NONE, MENU_DELETE_ID, Menu.NONE, R.string.delete);
	}
	    
	
	public void updateList() {
		closeAdapterCursor();
		Cursor cursor = null;
	    BookDBAdapter bookDBAdapter = new BookDBAdapter(this);
	    try{
	    	cursor = bookDBAdapter.getBooks();
	    	bookAdapter = new BookAdapter(this, cursor, 0);
		    setListAdapter(bookAdapter);
	    } catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			bookDBAdapter.close();
		}
	  
	}
	  
	  
	
	public void deleteBook(long id){
        Boolean deleted = false;
        BookDBAdapter bookDBAdapter = new BookDBAdapter(this);
	    try{
	    	deleted = bookDBAdapter.deleteBook(id);
	    } catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			bookDBAdapter.close();
		}
	
	    if(deleted){
	        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	        updateList();
	    } else{
	        Toast.makeText(this, R.string.book_not_deleted, Toast.LENGTH_SHORT).show();
	    }
	}
  
	  
	 /**
	 * Show form for add book.
	 *  
	 * @param View
	 */
	 public void onAddBookClicked(View v){ 
	    Intent i = new Intent(this, AddBookActivity.class);
	    startActivityForResult(i, REQUEST_ADD_BOOK);
	 }
			
			
			
	@Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
	 	String bookName = null;
	 	
	 	if(resultCode != RESULT_OK) return;
	 	
	 	switch(requestCode){
	 		
	 		case REQUEST_ADD_BOOK :
	 			bookName = data.getStringExtra(AddBookActivity.EXTRA_BOOK_NAME);
	 			onSaveNewBook(bookName);
	 		break;
	 		case REQUEST_EDIT_BOOK :
	 			bookName = data.getStringExtra(EditBookActivity.EXTRA_BOOK_NAME);
	 			long bookId =  data.getLongExtra( EditBookActivity.EXTRA_BOOK_ID, -1 ); 
	 			onSaveEditedBook( bookId , bookName);
	 		break;
	 		default:
	 			throw new Error("requestCode: " + requestCode + 
	 							" is not implemented in onActivityResult");
	 	} 
        super.onActivityResult(requestCode, resultCode, data);
    }
			
			
			
	public void onSaveEditedBook(long bookId, String bookName) {
		if(bookId == -1) throw new Error("Unable save edited book.");
		
		BookDBAdapter bookDbAdapter = new BookDBAdapter(this);
		boolean result = false;
		try {
			result = bookDbAdapter.editBook(bookId , bookName);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			bookDbAdapter.close();
		}
		
		if(result){
		    updateList();
		    Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_LONG).show();
	
		}else{
            Toast.makeText(this, R.string.saved_no, Toast.LENGTH_LONG).show();
        }
	}
	
	
	public void onSaveNewBook(String bookName) {
		BookDBAdapter bookDbAdapter = new BookDBAdapter(this);
		long id = -1;
		try {
			id = bookDbAdapter.insertBook(bookName);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			bookDbAdapter.close();
		}
		
		if(id > -1){
		    updateList();
		    Toast.makeText(this, R.string.book_added, Toast.LENGTH_LONG).show();		   
		}else{
            Toast.makeText(this, R.string.book_not_added, Toast.LENGTH_LONG).show();
        }
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
	
	@Override
	protected void onDestroy() {
		super.onStop();
		closeAdapterCursor();
	}
	
	private void closeAdapterCursor(){
		try {
			if(bookAdapter != null){
				if(!bookAdapter.getCursor().isClosed())
					bookAdapter.getCursor().close();
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
	
	/* OPTION MENU ---------------------------------------- */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_menu, menu);
	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_about:
	        startActivity(new Intent(this, AboutActivity.class));
	    	Log.d("MAINACTIVITY", "starting abotu...");
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    /* ENDOPTION MENU ---------------------------------------- */
}
