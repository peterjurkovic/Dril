package sk.peterjurkovic.dril;

import sk.peterjurkovic.dril.listener.OnEditWordListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class EditWordActivity extends FragmentActivity implements OnEditWordListener {
	
	
	public static final String EXTRA_QUESTION = "question";
	
	public static final String EXTRA_ANSWER = "answer";
	
	public static final String EXTRA_LECTURE_NAME = "lecturen_name";
	
	public static final String EXTRA_WORD_ID = "wordId";
	
	long wordId; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        wordId = getIntent().getLongExtra(EXTRA_WORD_ID, -1);
        
        setContentView(R.layout.word_edit_activity);
        
        Button cancel = (Button)findViewById(R.id.cancelEditQuestion);
        
        cancel.setVisibility(View.VISIBLE);
        
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(EditWordActivity.this, DashboardActivity.class) );
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
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    /* ENDOPTION MENU ---------------------------------------- */
}
