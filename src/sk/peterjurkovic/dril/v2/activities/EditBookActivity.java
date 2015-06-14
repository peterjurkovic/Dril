package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.dto.State;
import sk.peterjurkovic.dril.model.Book;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 3, 2013
 *
 */
public class EditBookActivity extends BaseBookActivity {
	
	private long bookId;
	public static final String EXTRA_BOOK_ID = "book_id";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, -1);
        setContentView(R.layout.v2_book_edit_layout);
        
        Button submit = (Button)findViewById(R.id.submitEdit);
        Button cancel = (Button)findViewById(R.id.cancelEdit);
        
        questionSpinner = (Spinner) findViewById(R.id.langQuestion);
        answerSpinner = (Spinner) findViewById(R.id.langAnswer);
        levelSpinner = (Spinner) findViewById(R.id.bookLevelEdit);
        shareCheckbox = (CheckBox) findViewById(R.id.bookShareEdit);
    	
        bookNameInput =  (EditText) findViewById(R.id.editBookName);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onSubmitClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onCancelClicked();
            	
            }
        });
        loadBookData();
    }
    


	@Override
	protected void onSubmitClicked() {
		 Book book = bookDao.getById(bookId);
		 if(book == null){
			 showMessage( R.string.err_book_notfound );
			 onCancelClicked();
			 return;
		 }
		 
		 book = prepareBoook(book);
		 if(book == null){
			 return;
		 }
		 
		 try{
     		bookDao.update(book);
     		onSuccess();
     	}catch(IllegalArgumentException e){
     		showMessage(R.string.err_book_exists );
     	}
	}
  
   
	public long getBookId(){
		return bookId;
	}
	
	public void loadBookData(){		
		Book book = bookDao.getById(bookId);
		if(book != null){
			prepareFormFor( book );
		}else{
			showMessage(R.string.err_book_notfound);
			finish();
		}
	}
	
	
	public void prepareFormFor(final Book book){
		bookNameInput.setText(book.getName());	
		ArrayAdapter<State> adapter = languageAdapter();
		questionSpinner.setAdapter(adapter);
		answerSpinner.setAdapter(adapter);
		if(book.getQuestionLang() != null){
			questionSpinner.setSelection(book.getQuestionLang().getId() - 1);
		}
		if(book.getAnswerLang() != null){
			answerSpinner.setSelection(book.getAnswerLang().getId() - 1);
		}
		levelSpinner.setAdapter( levelAdapter() );
		shareCheckbox.setChecked(book.isShared());
		if(book.getLevel() != null){
			levelSpinner.setSelection(book.getLevel().getId() - 1);
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	       goToParentActivity();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	
}
