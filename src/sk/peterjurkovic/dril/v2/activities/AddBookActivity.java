package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.model.Book;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 24, 2013
 * @version 2.0
 * 
 */
public class AddBookActivity extends BaseBookActivity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_book_add_layout);
        
        Button submit = (Button)findViewById(R.id.submitAdd);
        Button cancel = (Button)findViewById(R.id.cancelAdd);
        
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
        
       
        questionSpinner = (Spinner) findViewById(R.id.addLangQuestion);
        answerSpinner = (Spinner) findViewById(R.id.addLangAnswer);
        bookNameInput =  (EditText) findViewById(R.id.addBookName);
        levelSpinner = (Spinner) findViewById(R.id.addBookLevel);
        shareCheckbox = (CheckBox) findViewById(R.id.addBookShare);
        prepareSpinners();
    }

    
	@Override
	protected void onSubmitClicked() {
		final Book book = prepareBoook(new Book());
		 if(book == null){
			 return;
		 }
		try{
     		bookDao.create(book);
     		onSuccess();
     	}catch(IllegalArgumentException e){
     		showMessage(R.string.err_book_exists );
     	}
	}
	
	
	
}
