package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.dao.BookDao;
import sk.peterjurkovic.dril.dao.BookDaoImpl;
import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 * 
 */
public class AddBookActivity extends BaseActivity {
	

	private BookDao bookDao;
	private Spinner questionSpinner;
	private Spinner answerSpinner;
	private EditText bookNameInput;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_book_add_layout);
        
        Button submit = (Button)findViewById(R.id.submitAdd);
        Button cancel = (Button)findViewById(R.id.cancelAdd);
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
                onSubmitAddClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	onCancelAddClicked();
            }
        });
        
        bookDao = new BookDaoImpl(new BookDBAdapter( this ));
        questionSpinner = (Spinner) findViewById(R.id.addLangQuestion);
        answerSpinner = (Spinner) findViewById(R.id.addLangAnswer);
        bookNameInput =  (EditText) findViewById(R.id.addBookName);
        prepareSpinners();
    }

    
	public void onAddBook() {
		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}
	  
	public void onCancelAddClicked(){
		finish();
	}
	
	
	public void onSubmitAddClicked(){
	    String bookName = bookNameInput.getText().toString();
	    if(bookName.length() == 0){
	    	return;
	    };
	    Language langQuestion = (Language)questionSpinner.getSelectedItem();
    	Language langAnswer = (Language)answerSpinner.getSelectedItem();
    	if(langQuestion.equals(langAnswer)){
    		Toast.makeText(this, R.string.error_same_languages, Toast.LENGTH_LONG).show();
    	}
    	Book book = new Book();
	    book.setName(bookName);
    	book.setQuestionLang(langQuestion);
    	book.setAnswerLang(langAnswer);
    	bookDao.create(book);
    	onAddBook();
    	
	}
	
	
	private void prepareSpinners(){
		ArrayAdapter<Language> adapter = languageAdapter();
		questionSpinner.setAdapter(adapter);
		answerSpinner.setAdapter(adapter);
	}
	
	private ArrayAdapter<Language> languageAdapter(){
		ArrayAdapter<Language> adapter =  new ArrayAdapter<Language>(this,  R.layout.v2_spinner,  Language.getAll() );
		adapter.setDropDownViewResource(R.layout.v2_spinner_dropdown);	
		return adapter;
	}

	
}
