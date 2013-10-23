package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditBookActivity extends BaseActivity {
	
	private long bookId;
	
	private String bookName;
	
	public static final String EXTRA_BOOK_NAME = "book_name";
	
	public static final String EXTRA_BOOK_ID = "book_id";
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, -1);
        setContentView(R.layout.book_edit_activity);
        
        loadBookData();

        Button submit = (Button)findViewById(R.id.submitEdit);
        Button cancel = (Button)findViewById(R.id.cancelEdit);
        
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onSubmitEditBookClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onCancelEditBookClicked();
            	
            }
        });
        setGoHomePageListener();
    }
    
    
    private void onCancelEditBookClicked() {
		finish();
	}
    
    
    public void onSubmitEditBookClicked(){
        String bookName = ((EditText)findViewById(R.id.editBookName))
        											.getText().toString();
        if(bookName.length() == 0 || bookName.equals(this.bookName)) return;
        onSaveEditedBook(bookId , bookName);
    }

	public void onSaveEditedBook(long bookId, String bookName) {
		Intent result = new Intent();
		result.putExtra(EXTRA_BOOK_ID, bookId);
		result.putExtra(EXTRA_BOOK_NAME, bookName);
		setResult(RESULT_OK, result);
		finish();
		
	}
	
	public long getBookId(){
		return bookId;
	}
	
	public void loadBookData(){
		BookDBAdapter bookDBAdapter = new BookDBAdapter( this );
		Cursor cursor = null;
		try{
			cursor = bookDBAdapter.getBook(bookId);
			putBookData( cursor );
	    } catch (Exception e) {
			Log.d("EditBookFragment", "ERROR: " + e.getMessage());
		} finally {
			cursor.close();
			bookDBAdapter.close();
		}

	}
	
	
	public void putBookData( Cursor cursor){
		if(cursor.getCount() == 0){ 
			Toast.makeText(this, R.string.error_no_data, Toast.LENGTH_LONG).show();
			return;
		}
		cursor.moveToFirst();
		int bookNameIndex = cursor.getColumnIndex(BookDBAdapter.BOOK_NAME);
		bookName = cursor.getString(bookNameIndex);
		((EditText)findViewById(R.id.editBookName))
								.setText(bookName);		
		
	}
	
	

}
