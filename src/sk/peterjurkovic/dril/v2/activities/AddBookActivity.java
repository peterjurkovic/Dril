package sk.peterjurkovic.dril.v2.activities;


import sk.peterjurkovic.dril.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddBookActivity extends BaseActivity {
	
	public static final String EXTRA_BOOK_NAME = "book_name";
			
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_add_activity);
        
        Button submit = (Button)findViewById(R.id.submitAdd);
        Button cancel = (Button)findViewById(R.id.cancelAdd);
        
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmitAddClicked();
            }
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onCancelAddClicked();
            }
        });
        setGoHomePageListener();
    }

	public void onAddBook(String name) {
		Intent result = new Intent();
		result.putExtra(EXTRA_BOOK_NAME, name);
		setResult(RESULT_OK, result);
		finish();
		
	}
	  
	public void onCancelAddClicked(){
		finish();
	}
	
	public void onSubmitAddClicked(){
	    String bookName = ((EditText)findViewById(R.id.addBookName)).getText().toString();
	    if(bookName.length() == 0) return;
	    onAddBook(bookName);
	}

	
}
