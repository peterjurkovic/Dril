package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.listener.OnAddWordListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Peter Jurkovič
 * @date Oct 28, 2013
 *
 */
public class AddWordActivity extends BaseActivity implements OnAddWordListener{

public static final String EXTRA_QUESTION = "question";
	
	public static final String EXTRA_ANSWER = "answer";
	
	public static final String EXTRA_LECTURE_NAME = "lecturen_name";
	public static final String EXTRA_BOOK_ID = "bookId";
	
	private String wordLabel;
	private long bookId;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_word_add_activity_layout);
                
        wordLabel =  getString(R.string.word_add_label) + " " 
        		+ getIntent().getStringExtra(EXTRA_LECTURE_NAME);
        
        bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0);
        TextView header =  (TextView)findViewById(R.id.wordAddLabel);
        header.setText(wordLabel);
        
        Button cancel = (Button)findViewById(R.id.cancelAddQuestion);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	finish();
            }
        });
    }

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		wordLabel = savedInstanceState.getString(EXTRA_LECTURE_NAME);
		bookId = savedInstanceState.getLong(EXTRA_BOOK_ID);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_LECTURE_NAME, wordLabel);
		outState.putLong(EXTRA_BOOK_ID, bookId);
	}


	@Override
	public void saveNewWord(String question, String answer) {
		Intent result = new Intent();
		result.putExtra(EXTRA_QUESTION, question);
		result.putExtra(EXTRA_ANSWER, answer);
		setResult(RESULT_OK, result);
		finish();
		
	}
	
	public String getWordLabel(){
		return wordLabel;
	}
	
	public long getBookId(){
		return bookId;
	}
	
}
