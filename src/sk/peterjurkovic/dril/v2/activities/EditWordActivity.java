package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.listener.OnEditWordListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditWordActivity extends BaseActivity implements OnEditWordListener {
	
	public static final String EXTRA_QUESTION = "question";
	
	public static final String EXTRA_ANSWER = "answer";
	
	public static final String EXTRA_LECTURE_NAME = "lecturen_name";
	
	public static final String EXTRA_WORD_ID = "wordId";
	
	private long wordId; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        wordId = getIntent().getLongExtra(EXTRA_WORD_ID, -1);
        
        setContentView(R.layout.v2_word_edit_activity_layout);
        
        Button cancel = (Button)findViewById(R.id.cancelEditQuestion);
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });

    }
	
	
	@Override
	public void saveEditedWord(long wordId, String question, String answer) {
		Intent result = new Intent();
		result.putExtra(EXTRA_QUESTION, question);
		result.putExtra(EXTRA_ANSWER, answer);
		result.putExtra(EXTRA_WORD_ID, wordId);
		setResult(RESULT_OK, result);
		finish();
	}


	public long getWordId() {
		return wordId;
	}
	
}
