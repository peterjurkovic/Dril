package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.adapter.LectureAdapter;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.db.LectureDBAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LectureListActivity extends ListActivity{
	
	public static final String EXTRA_BOOK_ID= "bookId";
	
	public static final String TAG = "LectureListActivity";
	
	private static final int REQUEST_ADD_LECTURE = 0;
	private static final int REQUEST_EDIT_LECTURE = 1;
	
	public static final int MENU_VIEW_ID = Menu.FIRST +1;
	public static final int MENU_EDIT_ID = Menu.FIRST+2;
	public static final int MENU_DELETE_ID = Menu.FIRST+3;
	public static final int MENU_ACTIVE_RANDOM = Menu.FIRST+4;
	public static final int MENU_DEACTIVE_ALL = Menu.FIRST+5;
	
	private long bookId;
	private String bookName;
	
	LectureAdapter lectureAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.book_list_activity);
	    registerForContextMenu( getListView() );
	
	    
	    bookId = (long) getIntent().getLongExtra( EXTRA_BOOK_ID, 0);
	    
	    setContentView(R.layout.lecture_list_activity);
	    
	    
	    /* add new lecture listener */
	    Button addNewLecture = (Button)findViewById(R.id.addNewLecture);
	    addNewLecture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddLectureClicked();
			}
	    });
	    
	    bookName = getBookName(bookId);
	    
	    ((TextView)findViewById(R.id.lectureListLabel)).setText(bookName);
	    
	    updateList();
	    registerForContextMenu( getListView() );
	    
	    ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(LectureListActivity.this, DashboardActivity.class) );
            }
        });
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	    updateList();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		startWordActivity(id);
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case MENU_DELETE_ID:
        	deleteLecture(info.id);
            return true;
        case MENU_EDIT_ID:
        	onEditLectureClicked(info.id);
            return true;
        case MENU_VIEW_ID:
        	startWordActivity(info.id);
        	return true;
        case MENU_ACTIVE_RANDOM :
        	activeRandomWords(info.id,  10 ); // 10 - count of words
        return true;
        case MENU_DEACTIVE_ALL :
        	deactiveAllWordInLecture(info.id); 
        return true;    
        default:
                return super.onContextItemSelected(item);
        }
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle(R.string.options);
	    menu.add(Menu.NONE, MENU_VIEW_ID, Menu.NONE, R.string.view);
	    menu.add(Menu.NONE, MENU_EDIT_ID, Menu.NONE, R.string.edit);
	    menu.add(Menu.NONE, MENU_DELETE_ID, Menu.NONE, R.string.delete);
	    menu.add(Menu.NONE, MENU_ACTIVE_RANDOM, Menu.NONE, R.string.random_active_ten);
	    menu.add(Menu.NONE, MENU_DEACTIVE_ALL, Menu.NONE, R.string.lecture_deactive_all);
	}
	
	
	
	@Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        
	 	String lectureName = null;
	 	
	 	if(resultCode != RESULT_OK) return;
	 	
	 	switch(requestCode){
	 		
	 		case REQUEST_ADD_LECTURE :
	 			lectureName = data.getStringExtra(EditLectureActivity.EXTRA_LECTURE_NAME);
	 			onSaveNewLecture(lectureName);
	 		break;
	 		case REQUEST_EDIT_LECTURE :
	 			lectureName = data.getStringExtra(EditLectureActivity.EXTRA_LECTURE_NAME);
	 			long lectureId =  data.getLongExtra( EditLectureActivity.EXTRA_LECTURE_ID, -1 ); 
	 			onSaveEditedLecture( lectureId , lectureName);
	 		break;
	 		default:
	 			throw new Error("requestCode: " + requestCode + 
	 							" is not implemented in onActivityResult");
	 	} 
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	
	public void onSaveEditedLecture(long lectureId, String lectureName) {		
		if(lectureId == -1) throw new Error("Unable save edited lecture.");
		
		LectureDBAdapter lectureDbAdapter = new LectureDBAdapter(this);
		boolean result = false;
		try {
			result = lectureDbAdapter.editLecture(lectureId , lectureName);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			lectureDbAdapter.close();
		}
		
		if(result){
		    updateList();
		    Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_LONG).show();
		}else{
            Toast.makeText(this, R.string.saved_no, Toast.LENGTH_LONG).show();
        }
	}
		
	 
	 
	public void onEditLectureClicked(long lectureId) {
		Intent i = new Intent(this,  EditLectureActivity.class);
		i.putExtra(EditLectureActivity.EXTRA_LECTURE_ID, lectureId);
		startActivityForResult(i, REQUEST_EDIT_LECTURE);
	}
	
	
	public void updateList() {
		closeAdapterCursor();
		Cursor cursor = null;
	    LectureDBAdapter lectureDbAdapter = new LectureDBAdapter(this);
	    try{
	    	cursor = lectureDbAdapter.getLecturesByBookId( bookId );
	    	lectureAdapter = new LectureAdapter(this, cursor, 0);
	 	    setListAdapter(lectureAdapter);
	    } catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			lectureDbAdapter.close();
		}
	}
	
	
	
	public void deleteLecture(long id){
        Boolean deleted = false;
        LectureDBAdapter lectureDBAdapter = new LectureDBAdapter(this);
	    try{
	    	deleted = lectureDBAdapter.deleteLecture(id);
	    } catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			lectureDBAdapter.close();
		}
        
        if(deleted){
            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
            updateList();
        } else{
            Toast.makeText(this, R.string.lecture_not_deleted, Toast.LENGTH_SHORT).show();
        }
    }
	
	
	
	public void onSaveNewLecture(String lectureName) {
		LectureDBAdapter lectureBbAdapter = new LectureDBAdapter(this);
		long id = -1;
		try {
			id = lectureBbAdapter.insertLecture(bookId, lectureName);
		} catch (Exception e) {
			Log.d(TAG, "ERROR: " + e.getMessage());
		} finally {
			lectureBbAdapter.close();
		}
		if(id > -1){
		    updateList();
		    Toast.makeText(this, R.string.lecture_added, Toast.LENGTH_LONG).show();		   
		}else{
            Toast.makeText(this, R.string.lecture_not_added, Toast.LENGTH_LONG).show();
        }
	}
	
	
	
	public void onAddLectureClicked() { 
	    Intent i = new Intent(this, AddLectureActivity.class);
	    startActivityForResult(i, REQUEST_ADD_LECTURE);
	}
	
	
	
	public String getBookName( long bookId ){
		BookDBAdapter bookDBAdapter = new BookDBAdapter( this );
		Cursor cursor = null;
		String bookName = null;
		try{
			cursor = bookDBAdapter.getBook(bookId);
			if(cursor == null || cursor.getCount() == 0) return null;
				cursor.moveToFirst();
				int bookNameIndex = cursor.getColumnIndex(BookDBAdapter.BOOK_NAME);
				bookName = cursor.getString(bookNameIndex);
	    } catch (Exception e) {
			Log.d("EditBookFragment", "ERROR: " + e.getMessage());
		} finally {
			cursor.close();
			bookDBAdapter.close();
		}
		return bookName;
	}
	
	public void startWordActivity(long lectureId){
		Intent i = new Intent(this,  WordActivity.class);
		i.putExtra( WordActivity.LECTURE_ID_EXTRA, lectureId);
		startActivity(i);
	}
	
	
	public void activeRandomWords(long lectureId, int countOfwordToActivate ){
        WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
 	    try{
 	    	wordDbAdapter.activateWordRandomly(lectureId, countOfwordToActivate);
 	    } catch (Exception e) {
 			Log.d(TAG, "ERROR: " + e.getMessage());
 		} finally {
 			wordDbAdapter.close();
 		}
         
    	Toast.makeText(this, R.string.activated, Toast.LENGTH_SHORT).show();
    	updateList();
	}
	
	
	public void deactiveAllWordInLecture(long lectureId){
		WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
		boolean deactivated = false;
 	    try{
 	    	deactivated = wordDbAdapter.deactiveAllWordInLecture(lectureId);
 	    } catch (Exception e) {
 			Log.d(TAG, "ERROR: " + e.getMessage());
 		} finally {
 			wordDbAdapter.close();
 		}
 	    if(deactivated)	updateList();
 	    Toast.makeText(this, R.string.words_deactived, Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onStop();
		closeAdapterCursor();
	}
	
	private void closeAdapterCursor(){
		try {
			if(lectureAdapter != null){
				if(!lectureAdapter.getCursor().isClosed())
					lectureAdapter.getCursor().close();
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
