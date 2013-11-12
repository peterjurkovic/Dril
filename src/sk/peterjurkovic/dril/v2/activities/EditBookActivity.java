package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.dao.BookDao;
import sk.peterjurkovic.dril.dao.BookDaoImpl;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 3, 2013
 *
 */
public class EditBookActivity extends BaseActivity {
	
	private long bookId;
	public static final String EXTRA_BOOK_ID = "book_id";
	private BookDao bookDao;
	private Spinner questionSpinner;
	private Spinner answerSpinner;
	private EditText bookNameInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, -1);
        setContentView(R.layout.v2_book_edit_layout);
        bookDao = new BookDaoImpl(new BookDBAdapter( this ));
        
        Button submit = (Button)findViewById(R.id.submitEdit);
        Button cancel = (Button)findViewById(R.id.cancelEdit);
        
        questionSpinner = (Spinner) findViewById(R.id.langQuestion);
        answerSpinner = (Spinner) findViewById(R.id.langAnswer);
        bookNameInput =  (EditText) findViewById(R.id.editBookName);
        
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
        loadBookData();
    }
    
    
    private void onCancelEditBookClicked() {
		finish();
	}
    
   
    
    public void onSubmitEditBookClicked(){
        String bookName = bookNameInput.getText().toString();
        if(bookName.length() == 0) {
        	return;
        };
        Book book = bookDao.getById(bookId);
        if(book != null){
        	onSaveEditedBook();
        	book.setName(bookName);
        	Language langQuestion = (Language)questionSpinner.getSelectedItem();
        	Language langAnswer = (Language)answerSpinner.getSelectedItem();
        	if(langQuestion.equals(langAnswer)){
        		Toast.makeText(this, R.string.error_same_languages, Toast.LENGTH_LONG).show();
        		return;
        	}
        	book.setQuestionLang(langQuestion);
        	book.setAnswerLang(langAnswer);
        	bookDao.update(book);
        	onSaveEditedBook();
        }else{
        	Log.e("EDITBOOK", "Book with ID: " + bookId + " was not found");
        }
    }

	public void onSaveEditedBook() {
		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}
	
	public long getBookId(){
		return bookId;
	}
	
	public void loadBookData(){		
		Book book = bookDao.getById(bookId);
		if(book != null){
			putBookData( book );
		}else{
			Toast.makeText(this, R.string.error_no_data, Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void putBookData(final Book book){
		bookNameInput.setText(book.getName());	
		ArrayAdapter<Language> adapter = languageAdapter();
		questionSpinner.setAdapter(adapter);
		answerSpinner.setAdapter(adapter);
		if(book.getQuestionLang() != null){
			questionSpinner.setSelection(book.getQuestionLang().getId() - 1);
		}
		if(book.getAnswerLang() != null){
			answerSpinner.setSelection(book.getAnswerLang().getId() - 1);
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
	
	
	private ArrayAdapter<Language> languageAdapter(){
		ArrayAdapter<Language> adapter =  new ArrayAdapter<Language>(this,  R.layout.v2_spinner,  Language.getAll() );
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
	}
	

}
